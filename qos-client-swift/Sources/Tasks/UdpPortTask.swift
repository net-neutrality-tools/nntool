import Foundation
import CodableJSON
import nntool_shared_swift

class UdpPortTask: QoSBidirectionalIpTask {

    ///
    enum UdpPacketFlag: UInt8 {
        case oneDirection = 1
        case response = 2
        case awaitResponse = 3
    }

    var packetCountOut: Int?
    var packetCountIn: Int?

    var delayNs: UInt64 = 10 * NSEC_PER_MSEC // TODO: default?

    ///
    enum CodingKeys4: String, CodingKey {
        case packetCountOut = "out_num_packets"
        case packetCountIn = "in_num_packets"

        case delayNs = "delay"
    }

    private var packetCount: Int!

    private var receivedSequences = Set<UInt8>()
    private var packetsReceivedServer: Int?

    private var rttsNs: [String: UInt64]? // why string key?

    override var statusKey: String? {
        return "udp_result_status" // TODO: not yet implemented on server
    }

    override var objectiveTimeoutKey: String? {
        return "udp_objective_timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        let packetCount = direction == .outgoing ? packetCountOut : packetCountIn
        var plr: Double?

        if let p = packetCount {
            let lostPackets = p - receivedSequences.count
            plr = round(min(max(0, Double(lostPackets) / Double(receivedSequences.count)), 1) * 100)
        }

        var rttAvgNs: UInt64?
        if let rtts = rttsNs, rtts.count > 0 {
            rttAvgNs = rtts.compactMap { $0.value }.reduce(0, +) / UInt64(rtts.count)
        }
        let rttsJson = rttsNs?.mapValues { JSON($0) }

        r["udp_objective_delay"] = JSON(delayNs)

        switch direction {
        case .outgoing:
            r["udp_objective_out_port"] = JSON(portOut)
            r["udp_objective_out_num_packets"] = JSON(packetCountOut)

            r["udp_result_out_num_packets"] = JSON(packetsReceivedServer)
            r["udp_result_out_response_num_packets"] = JSON(receivedSequences.count)
            r["udp_result_out_packet_loss_rate"] = JSON(plr)

            r["udp_result_out_rtts_ns"] = JSON(rttsJson)
            r["udp_result_out_rtt_avg_ns"] = JSON(rttAvgNs)

        case .incoming:
            r["udp_objective_in_port"] = JSON(portIn)
            r["udp_objective_in_num_packets"] = JSON(packetCountIn)

            r["udp_result_in_num_packets"] = JSON(receivedSequences.count)
            r["udp_result_in_response_num_packets"] = JSON(packetsReceivedServer)
            r["udp_result_in_packet_loss_rate"] = JSON(plr)

            r["udp_result_in_rtts_ns"] = JSON(rttsJson)
            r["udp_result_in_rtt_avg_ns"] = JSON(rttAvgNs)

        default:
            break
        }

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        packetCountOut = try? container.decodeIfPresent(Int.self, forKey: .packetCountOut)
        if packetCountOut == nil {
            if let packetCountOutString = try? container.decodeIfPresent(String.self, forKey: .packetCountOut) {
                packetCountOut = Int(packetCountOutString)
            }
        }

        packetCountIn = try? container.decodeIfPresent(Int.self, forKey: .packetCountIn)
        if packetCountIn == nil {
            if let packetCountInString = try? container.decodeIfPresent(String.self, forKey: .packetCountIn) {
                packetCountIn = Int(packetCountInString)
            }
        }

        if let serverDelayNs = try? container.decodeIfPresent(UInt64.self, forKey: .delayNs) {
            self.delayNs = serverDelayNs
        }

        try super.init(from: decoder)
    }

    override public func taskMain() {
        guard
            let port = direction == .outgoing ? portOut : portIn,
            let packetCount = direction == .outgoing ? packetCountOut : packetCountIn
            else {
                taskLogger.error("udp requirements not satisfied")
                self.status = .error
                return
        }

        self.packetCount = packetCount

        if direction == .outgoing {
            rttsNs = [String: UInt64]()
        }

        // control connection request
        let cmd = String(format: "UDPTEST %@ %lu %lu +ID%d", direction.rawValue, port, packetCount, uid)

        do {
            let waitForAnswer = direction == .outgoing

            let response = try executeCommand(cmd: cmd, waitForAnswer: waitForAnswer)

            if waitForAnswer {
                guard let r = response, r.starts(with: "OK") else {
                    taskLogger.warning("controlconnection response is not ok: '\(String(describing: response))'")
                    self.status = .error
                    return
                }
            }
        } catch {
            taskLogger.warning("control connection response error")
            taskLogger.warning(error)
            self.status = .error
            return
        }

        ///

        let udpStreamUtilConfig = UdpStreamUtilConfiguration(
            host: controlConnectionParams.host,
            portOut: portOut,
            portIn: portIn,
            respondOnly: direction == .incoming,
            timeoutNs: timeoutNs,
            delayNs: delayNs,
            packetCount: packetCount
        )

        let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
        udpStreamUtil.delegate = self

        let udpStreamUtilStatus = udpStreamUtil.runStream()
        if udpStreamUtilStatus != .ok {
            status = udpStreamUtilStatus
            return
        }

        ///

        let resultCmd = String(format: "GET UDPRESULT %@ %lu +ID%d", direction.rawValue, port, uid)
        var resultCmdResponse: String?

        do {
            resultCmdResponse = try executeCommand(cmd: resultCmd, waitForAnswer: true)

            guard let r = resultCmdResponse, r.starts(with: "RCV") else {
                taskLogger.warning("controlconnection response2 is not RCV: ")
                taskLogger.warning(resultCmdResponse)
                status = .error
                return
            }
        } catch {
            taskLogger.warning("control connection response2 error")
            taskLogger.warning(error)
            status = .error
            return
        }

        if let components = resultCmdResponse?.components(separatedBy: " ") {
            if components.count < 2 {
                // command response could not be parsed
                status = .error
                return
            } else {
                packetsReceivedServer = Int(components[1])

                // incoming test rttsNs are provided by QoS service
                if direction == .incoming && components.count > 3 {
                    // we got rtts from qos-service as JSON
                    if let jsonData = components[3].data(using: .utf8) {
                        rttsNs = try? JSONDecoder().decode([String: UInt64].self, from: jsonData)
                    }
                }
            }
        }

        if status == .unknown {
            status = .ok
        }
    }

    ///
    private func packetDataByReplacingFlag(_ flag: UdpPacketFlag, data: Data) -> Data {
        var d = Data()

        d.append(flag.rawValue)
        d.append(data[1..<data.count])

        return d
    }
}

extension UdpPortTask: UdpStreamUtilDelegate {

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didBindToLocalPort port: UInt16) {
        // do nothing
    }

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, willSendPacketWithNumer packetNum: Int) -> (Data, UdpStreamUtil.Tag)? {
        var data = Data()

        data.append(UdpPacketFlag.awaitResponse.rawValue)
        data.append(UInt8(truncatingIfNeeded: packetNum))

        if let uuidData = extractUuidFromToken().data(using: .utf8) { /* .ascii ? */
            data.append(uuidData)
        }

        let currentTimeNs = TimeHelper.currentTimeNs()
        var currentTimeNsBigEndian = currentTimeNs.bigEndian
        data.append(UnsafeBufferPointer(start: &currentTimeNsBigEndian, count: 1))

        return (data, .outgoing)
    }

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didReceiveData data: Data, fromAddress address: Data, atTimestamp timestamp: UInt64) -> Bool {

        guard data.count >= 46 /*1+1+36+8*/ else {
            status = .error // UDP packet too short
            return true
        }

        let flag = ByteUtil.convertData(data[0..<1], to: UInt8.self)

        guard let flagEnum = UdpPacketFlag(rawValue: flag) else {
            taskLogger.debug("received unknown packet flag \(flag)")
            status = .error // unknown packet flag -> return
            return true
        }
        taskLogger.debug("received flag: \(flagEnum)")

        let sequenceNum = ByteUtil.convertData(data[1..<2], to: UInt8.self)
        taskLogger.debug("received sequenceNum: \(sequenceNum)")

        /*let uuidData = data[2..<38]
        let uuid = String(data: uuidData, encoding: .utf8)
        taskLogger.debug("uuid: \(uuid)")*/

        if direction == .outgoing {
            //let sentTimeNs = ByteUtil.convertData(data[38..<46], to: UInt64.self) // Fails with "Fatal error: load from misaligned raw pointer"
            let sentTimeNs = data[38..<46].withUnsafeBytes { UInt64(bigEndian: $0.pointee) }
            rttsNs?["\(sequenceNum)"] = timestamp - sentTimeNs
        }

        if receivedSequences.contains(sequenceNum) {
            taskLogger.warning("received duplicate")
            status = .error
            return true
        } else {
            receivedSequences.insert(sequenceNum)

            if direction == .outgoing {
                assert(flagEnum == UdpPacketFlag.response)

                if receivedSequences.count == packetCount {
                     return true
                }
            } else {
                assert(flagEnum == UdpPacketFlag.awaitResponse)

                udpStreamUtil.sendResponse(packetDataByReplacingFlag(.response, data: data), toAddress: address, tag: .incomingResponse)

                if receivedSequences.count == packetCount {
                    // add delay to allow the last confirmation packet to reach the server
                    usleep(UInt32(delayNs / NSEC_PER_USEC))
                    return true
                }
            }
        }

        return false
    }
}
