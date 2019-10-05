import Foundation
import CodableJSON

class UdpPortTask: QoSBidirectionalIpTask {

    var packetCountOut: Int?
    var packetCountIn: Int?

    var delayNs: UInt64 = 10 * NSEC_PER_MSEC // TODO: default?

    ///
    enum CodingKeys4: String, CodingKey {
        case packetCountOut = "out_num_packets"
        case packetCountIn = "in_num_packets"

        case delayNs = "delay"
    }

    private var receivedSequences = Set<UInt8>()
    private var packetsReceivedServer: Int?

    private var rttsNs: [String: UInt64]?

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
            rttAvgNs = rtts.reduce(0) { $0 + $1.value } / UInt64(rtts.count)
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

        packetCountOut = try container.decodeIfPresent(Int.self, forKey: .packetCountOut)
        if packetCountOut == nil {
            if let packetCountOutString = try container.decodeIfPresent(String.self, forKey: .packetCountOut) {
                packetCountOut = Int(packetCountOutString)
            }
        }

        packetCountIn = try container.decodeIfPresent(Int.self, forKey: .packetCountIn)
        if packetCountIn == nil {
            if let packetCountInString = try container.decodeIfPresent(String.self, forKey: .packetCountIn) {
                packetCountIn = Int(packetCountInString)
            }
        }

        if let serverDelayNs = try container.decodeIfPresent(UInt64.self, forKey: .delayNs) {
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

        ///
        let udpStreamUtilConfig = UdpStreamUtilConfiguration(
            host: controlConnectionParams.host,
            port: port,
            outgoing: direction == .outgoing,
            timeoutNs: timeoutNs,
            delayNs: delayNs,
            packetCount: packetCount,
            uuid: extractUuidFromToken(),
            payload: nil
        )

        let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
        ///

        // control connection request
        let cmd = String(format: "UDPTEST %@ %lu %lu +ID%d", direction.rawValue, port, packetCount, uid)

        do {
            let waitForAnswer = direction == .outgoing

            let response = try executeCommand(cmd: cmd, waitForAnswer: waitForAnswer)

            if waitForAnswer {
                guard let r = response, r.starts(with: "OK") else {
                    taskLogger.warning("controlconnection response is not ok: ")
                    taskLogger.warning(response)
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
        let (streamUtilStatus, result) = udpStreamUtil.runStream()
        ///

        status = QoSTaskStatus(rawValue: streamUtilStatus.rawValue) ?? .error

        if let r = result {
            receivedSequences = r.receivedSequences
            rttsNs = r.rttsNs
        }

        ////////

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
                        /*if let jsonDict = ((try? JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: UInt64]) as [String: UInt64]??) {
                            rttsNs = jsonDict
                        }*/
                    }
                }
            }
        }

        if status == .unknown {
            status = .ok
        }
    }
}
