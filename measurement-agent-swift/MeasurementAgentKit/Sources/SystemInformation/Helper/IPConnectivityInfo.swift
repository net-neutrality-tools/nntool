/*******************************************************************************
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
 ******************************************************************************/

import Foundation
import CocoaAsyncSocket

public struct IPStatus {
    public var localAddress: String?
    public var publicAddress: String?

    public var hasInternetConnection = false

    public var isNat: Bool {
        return localAddress != publicAddress
    }
}

extension IPStatus: CustomDebugStringConvertible {

    public var debugDescription: String {
        return "[local: \(String(describing: localAddress)), public: \(String(describing: publicAddress)), conn: \(hasInternetConnection), nat: \(isNat)]"
    }
}

public struct ConnectivityStatus {
    public var ipv4: IPStatus?
    public var ipv6: IPStatus?
}

public class IPConnectivityInfo {

    let controlServiceV4: ControlService
    let controlServiceV6: ControlService

    init(controlServiceV4: ControlService, controlServiceV6: ControlService) {
        self.controlServiceV4 = controlServiceV4
        self.controlServiceV6 = controlServiceV6
    }

    public func checkIPv4Connectivity() -> IPStatus? {
        let semaphore = DispatchSemaphore(value: 0)

        var ipv4Status = IPStatus()

        ipv4Status.localAddress = getLocalIPv4Address()

        DispatchQueue.main.async {
            self.controlServiceV4.getIp(onSuccess: { response in
                guard let version = response.ipVersion, version == .ipv4 else {
                    return
                }

                ipv4Status.publicAddress = response.ipAddress
                ipv4Status.hasInternetConnection = true

                semaphore.signal()
            }, onFailure: { _ in
                semaphore.signal()
            })
        }

        _ = semaphore.wait(timeout: .now() + .seconds(2))

        return ipv4Status
    }

    public func checkIPv6Connectivity() -> IPStatus? {
        let semaphore = DispatchSemaphore(value: 0)

        var ipv6Status = IPStatus()

        ipv6Status.localAddress = getLocalIPv6Address()

        if ipv6Status.localAddress != nil {
            DispatchQueue.main.async {
                self.controlServiceV6.getIp(onSuccess: { response in
                    guard let version = response.ipVersion, version == .ipv6 else {
                        return
                    }

                    ipv6Status.publicAddress = response.ipAddress
                    ipv6Status.hasInternetConnection = true

                    semaphore.signal()
                }, onFailure: { _ in
                    semaphore.signal()
                })
            }

            _ = semaphore.wait(timeout: .now() + .seconds(3))
        }

        return ipv6Status
    }

    public func getLocalIPv4Address() -> String? {
        return getLocalActiveIpAddressFromUdpSocket(family: AF_INET)
    }

    public func getLocalIPv6Address() -> String? {
        return getLocalActiveIpAddressFromUdpSocket(family: AF_INET6)
    }

    public func getLocalActiveIpAddressFromUdpSocket(family: Int32) -> String? {
        var host: String?

        switch family {
        case AF_INET6:
            host = controlServiceV6.service.baseURL?.host
        default:
            host = controlServiceV4.service.baseURL?.host
        }

        if let h = host {
            if let socketIp = SocketIpHelper().getIpFromSocket(family: family, host: h, timeoutMs: 1000) {
                logger.debug("Got IP from socket: \(socketIp)")
                return socketIp
            }
        }

        return getLocalActiveIpAddress(family: family)
    }

    public func getLocalActiveIpAddress(family: Int32) -> String? {
        var addrs: UnsafeMutablePointer<ifaddrs>?
        guard getifaddrs(&addrs) == 0 else {
            return nil
        }

        var address: String?

        var cursor = addrs
        while cursor != nil {
            defer {
                cursor = cursor?.pointee.ifa_next
            }

            guard let iface = cursor?.pointee else {
                continue
            }

            let saFamily = iface.ifa_addr.pointee.sa_family

            guard saFamily == family else {
                continue
            }

            let flags = Int32(iface.ifa_flags)

            guard flags & (IFF_UP|IFF_RUNNING|IFF_LOOPBACK) == (IFF_UP|IFF_RUNNING) else {
                continue
            }

            // skip link local addresses
            guard !isLinkLocal(addr: iface.ifa_addr, family: family) else {
                logger.debug("Skipping ip address \(ifaAddrToString(iface.ifa_addr)) because it is link-local")
                continue
            }

            // TODO: check for interface type (name.hasPrefix("en") => wifi, name.hasPrefix("pdp_ip") => wwan)

            if let ip = ifaAddrToString(iface.ifa_addr) {
                address = ip
                logger.debug("Found ip address \(address)")
                break
            }
        }

        freeifaddrs(addrs)

        return address
    }

    private func ifaAddrToString(_ addr: UnsafeMutablePointer<sockaddr>) -> String? {
        var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
        let addrSaLen = socklen_t(addr.pointee.sa_len)
        if getnameinfo(addr, addrSaLen, &hostname, socklen_t(hostname.count), nil, socklen_t(0), NI_NUMERICHOST) == 0 {
            return String(cString: hostname)
        }

        return nil
    }

    private func isLinkLocal(addr: UnsafeMutablePointer<sockaddr>, family: Int32) -> Bool {
        switch family {
        case AF_INET:
            let saddr = addr.withMemoryRebound(to: sockaddr_in.self, capacity: 1) {
                $0.pointee.sin_addr.s_addr
            }
            return (saddr & IN_CLASSB_NET.bigEndian) == IN_LINKLOCALNETNUM.bigEndian
        case AF_INET6:
            let addr8u6 = addr.withMemoryRebound(to: sockaddr_in6.self, capacity: 1) {
                $0.pointee.sin6_addr.__u6_addr.__u6_addr8
            }
            return addr8u6.0 == 0xfe && (addr8u6.1 & 0xc0) == 0x80
        default: return false
        }
    }
}

class SocketIpHelper: NSObject {

    private var semaphore = DispatchSemaphore(value: 0)
    private var family: Int32!
    private var ipAddress: String?

    func getIpFromSocket(family: Int32, host: String, timeoutMs: Int) -> String? {
        self.family = family

        let udpSocket = GCDAsyncUdpSocket(delegate: self, delegateQueue: DispatchQueue.global(qos: .default))

        do {
            try udpSocket.connect(toHost: host, onPort: 11111)
        } catch {
            return nil
        }

        semaphore.wait(timeout: .now() + .microseconds(timeoutMs))

        return ipAddress
    }
}

extension SocketIpHelper: GCDAsyncUdpSocketDelegate {
    func udpSocket(_ sock: GCDAsyncUdpSocket, didConnectToAddress address: Data) {
        switch family {
        case AF_INET6:
            ipAddress = sock.localHost_IPv6()
        default:
            ipAddress = sock.localHost_IPv4()
        }

        semaphore.signal()
    }

    func udpSocket(_ sock: GCDAsyncUdpSocket, didNotConnect error: Error?) {
        semaphore.signal()
    }

    func udpSocketDidClose(_ sock: GCDAsyncUdpSocket, withError error: Error?) {
        semaphore.signal()
    }
}
