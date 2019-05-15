import Foundation

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

    override var statusKey: String {
        return "udp_result_status" // TODO: not yet implemented on server
    }

    override var result: QoSTaskResult {
        var r = super.result

        // TODO: set objective values?

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

        switch direction {
        case .outgoing:
            r["udp_result_out_num_packets"] = packetsReceivedServer
            r["udp_result_out_response_num_packets"] = receivedSequences.count
            r["udp_result_out_packet_loss_rate"] = plr

            r["udp_result_out_rtts_ns"] = rttsNs
            r["udp_result_out_rtt_avg_ns"] = rttAvgNs

        case .incoming:
            r["udp_result_in_num_packets"] = receivedSequences.count
            r["udp_result_in_response_num_packets"] = packetsReceivedServer
            r["udp_result_in_packet_loss_rate"] = plr

            r["udp_result_in_rtts_ns"] = rttsNs
            r["udp_result_in_rtt_avg_ns"] = rttAvgNs

        default:
            break
        }

        return r
    }

    ///
    override init?(config: QoSTaskConfiguration) {
        if let packetCountOutString = config[CodingKeys4.packetCountOut.rawValue] as? String, let packetCountOut = Int(packetCountOutString) {
            self.packetCountOut = packetCountOut
        }

        if let packetCountInString = config[CodingKeys4.packetCountIn.rawValue] as? String, let packetCountIn = Int(packetCountInString) {
            self.packetCountIn = packetCountIn
        }

        if let delayNsString = config[CodingKeys4.delayNs.rawValue] as? String, let delayNs = UInt64(delayNsString) {
            self.delayNs = delayNs
        }

        super.init(config: config)
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        if let packetCountOutString = try container.decodeIfPresent(String.self, forKey: .packetCountOut) {
            packetCountOut = Int(packetCountOutString)
        }

        if let packetCountInString = try container.decodeIfPresent(String.self, forKey: .packetCountIn) {
            packetCountIn = Int(packetCountInString)
        }

        if let delayNsString = try container.decodeIfPresent(String.self, forKey: .delayNs), let delayNs = UInt64(delayNsString) {
            self.delayNs = delayNs
        }

        try super.init(from: decoder)
    }

    override public func main() {
        guard
            let port = direction == .outgoing ? portOut : portIn,
            let packetCount = direction == .outgoing ? packetCountOut : packetCountIn
            else {
                logger.error("udp requirements not satisfied")
                self.status = .error
                return
        }

        // TODO: check if all needed parameters are there and fail otherwise

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
                    logger.warning("controlconnection response is not ok: ")
                    logger.warning(response)
                    self.status = .error
                    return
                }
            }
        } catch {
            logger.warning("control connection response error")
            logger.warning(error)
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
                logger.warning("controlconnection response2 is not RCV: ")
                logger.warning(resultCmdResponse)
                status = .error
                return
            }
        } catch {
            logger.warning("control connection response2 error")
            logger.warning(error)
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
                        if let jsonDict = ((try? JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: UInt64]) as [String: UInt64]??) {
                            rttsNs = jsonDict
                        }
                    }
                }
            }
        }

        if status == .unknown {
            status = .ok
        }
    }
}
