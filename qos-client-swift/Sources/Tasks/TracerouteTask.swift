/***************************************************************************
 * Copyright 2017 appscape gmbh
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

import Foundation
import CodableJSON
import nntool_shared_swift
import QoSKitTraceroute
import Socket

// TODO: Add IPv6 support
class TracerouteTask: QoSTask {

    private var host: String
    private var maxHops: UInt8 = 64
    private var isReverse = false
    private var authToken: String?
    private var authTimestamp: UInt64?
    
    //

    private static let hopTimeoutS = 2
    private static var hopTimeoutNs = UInt64(hopTimeoutS) * NSEC_PER_SEC

    private let shouldMaskIpAddress = false // IP addresses will be anonymized on the server
    private let ipAddressMaskParts = 1

    private let minPort = 32768

    //

    private var hopDetails: [JSON]?
    private var maxHopsExceeded = false
    private var timedOut = false

    ///
    enum CodingKeys4: String, CodingKey {
        case host
        case maxHops = "max_hops"
        case isReverse = "is_reverse"
        case authToken = "auth_token"
        case authTimestamp = "auth_timestamp"
    }

    /*override var statusKey: String? {
        return "traceroute_result_status"
    }*/

    override var objectiveTimeoutKey: String? {
        return "traceroute_objective_timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["traceroute_objective_host"] = JSON(host)
        r["traceroute_objective_max_hops"] = JSON(maxHops)
        r["traceroute_objective_is_reverse"] = JSON(isReverse)
        
        if maxHopsExceeded {
            r["traceroute_result_status"] = JSON("MAX_HOPS_EXCEEDED")
        } else if timedOut {
            r["traceroute_result_status"] = JSON(QoSTaskStatus.timeout.rawValue)
        } else {
            r["traceroute_result_status"] = JSON(QoSTaskStatus.ok.rawValue)
        }

        if let hops = hopDetails, hops.count > 0 {
            r["traceroute_result_hops"] = JSON(hops.count)
            r["traceroute_result_details"] = JSON(hops)
        } else {
            r["traceroute_result_status"] = JSON(QoSTaskStatus.error.rawValue)
        }

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        host = try container.decode(String.self, forKey: .host)

        if let serverMaxHops = container.decodeIfPresentWithStringFallback(UInt8.self, forKey: .maxHops) {
            maxHops = serverMaxHops
        }

        isReverse = container.decodeIfPresentWithStringFallback(Bool.self, forKey: .isReverse) ?? false
        
        authToken = try? container.decodeIfPresent(String.self, forKey: .authToken)
        authTimestamp = try? container.decodeIfPresentWithStringFallback(UInt64.self, forKey: .authTimestamp)

        try super.init(from: decoder)
    }

    override func taskMain() {
        progress.totalUnitCount = Int64(maxHops)

        if isReverse {
            runReverseTraceroute()
        } else {
            runTraceroute()
        }
    }

    func runReverseTraceroute() {
        guard let url = URL(string: host) else {
            status = .error
            return
        }

        //let startedAt = TimeHelper.currentTimeNs()
        let semaphore = DispatchSemaphore(value: 0)
        
        let configuration = URLSessionConfiguration.ephemeral

        configuration.allowsCellularAccess = true
        configuration.timeoutIntervalForResource = TimeInterval(timeoutS)
        configuration.timeoutIntervalForRequest = TimeInterval(timeoutS)

        var additonalHeaderFields = [String: String]()

        // Set user agent
        if let userAgent = UserDefaults.standard.string(forKey: "UserAgent") {
            additonalHeaderFields["User-Agent"] = userAgent
        }

        configuration.httpAdditionalHeaders = additonalHeaderFields

        let urlSession = URLSession(configuration: configuration, delegate: self, delegateQueue: nil)
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        var tracerouteBody = [
            "cmd": "traceroute",
            "max_hops": maxHops
            ] as [String : Any]
        
        if let tk = authToken, let ts = authTimestamp {
            tracerouteBody["tk"] = tk
            tracerouteBody["ts"] = "\(ts)"
            //tracerouteBody["ts"] = ts
        }
        
        request.httpBody = try? JSONSerialization.data(withJSONObject: tracerouteBody)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        request.addValue(host, forHTTPHeaderField: "Origin") // ias-server needs this to be set...
        request.timeoutInterval = TimeInterval(timeoutS)

        let dataTask = urlSession.dataTask(with: request) { (data, response, error) in
            if error != nil {
                self.status = .error
            } else {
                if let data = data {
                    guard let reverseTracerouteResponse = try? JSONDecoder().decode(ReverseTracerouteResponse.self, from: data) else {
                        self.status = .error
                        semaphore.signal()
                        return
                    }
                    
                    self.hopDetails = reverseTracerouteResponse.hops.map {
                        JSON([
                            "host": JSON($0.ip)
                        ])
                    }
                    
                    self.status = .ok
                }
            }

            semaphore.signal()
        }

        dataTask.resume()

        if semaphore.wait(timeout: .now() + .nanoseconds(Int(timeoutNs))) == .timedOut {
            status = .timeout
        }
    }
    
    func runTraceroute() {
        let startedAt = TimeHelper.currentTimeNs()
        
        var addr = sockaddr_in()
        addr.sin_family = sa_family_t(AF_INET)

        let hostCString = host.cString(using: .utf8) // .ascii?
        addr.sin_addr.s_addr = inet_addr(hostCString)

        if addr.sin_addr.s_addr == INADDR_NONE {
            guard let hostinfo = gethostbyname(hostCString) else {
                status = .error

                let errorString = String(cString: strerror(h_errno), encoding: .ascii)
                taskLogger.debug("DNS resolution failed: \(String(describing: errorString))")

                return
            }

            memcpy(&addr.sin_addr, hostinfo.pointee.h_addr_list[0], Int(hostinfo.pointee.h_length))
        }

        taskLogger.debug("Starting UDP traceroute test to destination \(host)")

        let receiveSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_ICMP)
        guard receiveSocket != -1 else {
            status = .error
            return
        }

        guard fcntl(receiveSocket, F_SETFL, O_NONBLOCK) != -1 else {
            status = .error
            close(receiveSocket)
            return
        }

        let sendSocket = socket(AF_INET, SOCK_DGRAM, IPPROTO_IP)
        guard sendSocket != -1 else {
            status = .error
            close(receiveSocket)
            return
        }

        // bind to thread-unique source port
        var bindAddr = sockaddr_in()
        bindAddr.sin_family = sa_family_t(AF_INET)
        bindAddr.sin_len = __uint8_t(MemoryLayout<sockaddr_in>.size)
        bindAddr.sin_addr.s_addr = INADDR_ANY.bigEndian

        let tid = pthread_mach_thread_np(pthread_self())
        bindAddr.sin_port = UInt16((0xffff & tid) | 0x8000).bigEndian

        let bindResult = withUnsafePointer(to: &bindAddr) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1, {
                return bind(sendSocket, $0, socklen_t(MemoryLayout<sockaddr_in>.stride))
            })
        }

        guard bindResult != -1 else {
            status = .error
            close(receiveSocket)
            close(sendSocket)
            return
        }

        var ttl = 1
        var ipAddr: in_addr_t = 0

        hopDetails = [JSON]()
        var hopResult: JSON?

        repeat {
            addr.sin_port = UInt16(minPort + ttl - 1).bigEndian

            hopResult = traceWithSendSock(sendSocket, recvSock: receiveSocket, ttl: ttl, port: bindAddr.sin_port, sockAddr: addr, ipAddr: &ipAddr)
            if let hop = hopResult {
                hopDetails?.append(hop)
                progress.completedUnitCount += 1
            }

            ttl += 1
            guard ttl <= maxHops else {
                taskLogger.warning("Traceroute reached max hops (\(ttl) > \(maxHops))")
                maxHopsExceeded = true
                break
            }

            guard TimeHelper.currentTimeNs() - startedAt <= timeoutNs else {
                taskLogger.warning("Traceroute timed out after \(timeoutNs)ns")
                timedOut = true
                break
            }

        } while hopResult != nil && !isCancelled && ipAddr != addr.sin_addr.s_addr

        close(sendSocket)
        close(receiveSocket)

        if hopResult == nil {
            hopDetails = nil
        }
    }

    func traceWithSendSock(_ sendSock: Int32, recvSock: Int32, ttl: Int, port: in_port_t, sockAddr: sockaddr_in, ipAddr: inout in_addr_t) -> JSON? {
        var mutableTttl = ttl
        var mutableAddr = sockAddr

        let t = setsockopt(sendSock, IPPROTO_IP, IP_TTL, &mutableTttl, socklen_t(MemoryLayout<Int>.size))
        guard t != -1 else {
            return nil
        }

        var storageAddr = sockaddr_in()
        var n = socklen_t(MemoryLayout<sockaddr>.size)
        var payload = CChar(ttl & 0xFF)

        let startTime = TimeHelper.currentTimeNs()

        let sendto_result = withUnsafePointer(to: &mutableAddr) {
            $0.withMemoryRebound(to: sockaddr.self, capacity: 1, {
                return sendto(sendSock, &payload, MemoryLayout<CChar>.size, 0, $0, socklen_t(MemoryLayout<sockaddr>.stride))
            })
        }

        guard sendto_result == MemoryLayout<CChar>.size else {
            return nil
        }

        while (TimeHelper.currentTimeNs() - startTime) < TracerouteTask.hopTimeoutNs {
            var tv = timeval()

            tv.tv_sec = TracerouteTask.hopTimeoutS
            tv.tv_usec = 0

            var readfds = fd_set()
            readfds.zero()
            readfds.set(recvSock)

            let ret = Darwin.select(recvSock + 1, &readfds, nil, nil, &tv)
            let hopDurationNs = TimeHelper.currentTimeNs() - startTime

            if ret < 0 {
                return nil
            } else if ret == 0 {
                break
            } else {
                guard readfds.isSet(recvSock) else {
                    break
                }

                var buf = [Int8](repeating: 0, count: 512)

                let len = withUnsafeMutablePointer(to: &storageAddr) {
                    $0.withMemoryRebound(to: sockaddr.self, capacity: 1, { (a: UnsafeMutablePointer<sockaddr>) in
                        return Darwin.recvfrom(recvSock, &buf, buf.count, 0, a, &n)
                    })
                }

                guard len >= 0 else {
                    return nil
                }

                guard let ipHeader: ip = (buf.withUnsafeBytes { bufPtr in
                    return bufPtr.baseAddress?.bindMemory(to: ip.self, capacity: 1).pointee
                }) else {
                    break
                }

                let hlen = Int32(ipHeader.ip_hl << 2)

                guard len >= hlen + ICMP_MINLEN else {
                   break
                }

                var ips = [Int8](repeating: 0, count: 16)

                _ = inet_ntop(AF_INET, &storageAddr.sin_addr.s_addr, &ips, socklen_t(ips.count)) // TODO: check return value?

                guard let icmpHeader: icmp = (buf.withUnsafeBytes { bufPtr in
                    return bufPtr.baseAddress?.advanced(by: Int(hlen)).bindMemory(to: icmp.self, capacity: 1).pointee
                }) else {
                    break
                }

                let icmpType = icmpHeader.icmp_type
                let icmpCode = icmpHeader.icmp_code

                if (icmpType == ICMP_TIMXCEED && icmpCode == ICMP_TIMXCEED_INTRANS) || icmpType == ICMP_UNREACH {
                    var icmpIpHeader = icmpHeader.icmp_dun.id_ip.idi_ip
                    let innerHlen = Int32(icmpIpHeader.ip_hl << 2)

                    if icmpIpHeader.ip_p == IPPROTO_UDP {
                        guard let udpHeader: udphdr = (buf.withUnsafeBytes { bufPtr in
                            // TODO: why +8?
                            return bufPtr.baseAddress?.advanced(by: Int(hlen + innerHlen) + 8).bindMemory(to: udphdr.self, capacity: 1).pointee
                        }) else {
                            break
                        }

                        let matching = udpHeader.uh_sport == port && udpHeader.uh_dport == UInt16(minPort+ttl-1).bigEndian
                        if matching {
                            ipAddr = storageAddr.sin_addr.s_addr

                            guard let remoteAddress = String(cString: &ips, encoding: .utf8) else {
                                break
                            }

                            let hopHost = shouldMaskIpAddress ? maskIp(ip: remoteAddress, parts: ipAddressMaskParts): remoteAddress

                            taskLogger.info("Adding hop (host=\"\(hopHost)\", time=\(hopDurationNs))")

                            return JSON([
                                "host": JSON(hopHost),
                                "time": JSON(hopDurationNs)
                            ])
                        }
                    }
                }
            }
        }

        let hopDurationNs = TimeHelper.currentTimeNs() - startTime

        taskLogger.info("Adding hop (host=\"*\", time=\(hopDurationNs)")

        return JSON([
            "host": JSON("*"),
            "time": JSON(hopDurationNs)
        ])
    }

    func maskIp(ip: String, parts: Int) -> String {
        guard parts > 0 else {
            return ip
        }

        var components = ip.components(separatedBy: ".")

        let maskParts = min(parts, components.count)

        for i in stride(from: components.count - 1, to: components.count - 1 - maskParts, by: -1) {
            components[i] = "x"
        }

        return components.joined(separator: ".")
    }
}

extension TracerouteTask: URLSessionTaskDelegate {

    func urlSession(_ session: URLSession, task: URLSessionTask, willPerformHTTPRedirection response: HTTPURLResponse, newRequest request: URLRequest, completionHandler: @escaping (URLRequest?) -> Void) {

        // prevent redirects
        completionHandler(nil)
    }
}

class ReverseTracerouteResponse: Codable {

    var hops: [HopDetail]

    ///
    enum CodingKeys: String, CodingKey {
        case hops
    }

    class HopDetail: Codable {
        var id: String
        var ip: String

        ///
        enum CodingKeys: String, CodingKey {
            case ip
            case id
        }
    }
}
