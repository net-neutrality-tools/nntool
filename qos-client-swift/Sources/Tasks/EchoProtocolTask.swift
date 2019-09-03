import Foundation
import CodableJSON

class EchoProtocolTask: QoSTask {

    public enum ProtocolType: String, Codable {
        case tcp // = "tcp"
        case udp // = "udp"
    }

    private var host: String
    private var port: UInt16 = 7
    private var protocolType: ProtocolType = .udp
    private var payload: String

    private let packetCount = 1 // TODO: config (echo protocol test currently only works with one packet)
    private let delayNs = 1 * NSEC_PER_SEC

    ///
    enum CodingKeys4: String, CodingKey {
        case host
        case port
        case protocolType = "protocol"
        case payload
    }

    private var resultResponse: String?
    private var rttNs: UInt64?

    override var statusKey: String {
        return "echo_protocol_status"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["echo_protocol_objective_server_addr"] = JSON(host)
        r["echo_protocol_objective_server_port"] = JSON(port)
        r["echo_protocol_objective_protocol"] = JSON(protocolType.rawValue)
        r["echo_protocol_objective_payload"] = JSON(payload)

        r["echo_protocol_result"] = JSON(resultResponse)
        r["echo_protocol_result_rtt_ns"] = JSON(rttNs)

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        host = try container.decode(String.self, forKey: .host)
        payload = try container.decode(String.self, forKey: .payload)

        if let portString = try container.decodeIfPresent(String.self, forKey: .port), let port = UInt16(portString) {
            self.port = port
        }

        if let pType = try container.decodeIfPresent(ProtocolType.self, forKey: .protocolType) {
            self.protocolType = pType
        }

        try super.init(from: decoder)
    }

    ///
    override public func main() {
        switch protocolType {
        case .tcp:
            let tcpStreamUtilConfig = TcpStreamUtilConfiguration(
                host: host,
                port: port,
                outgoing: true,
                timeoutNs: timeoutNs,
                payload: payload
            )

            let tcpStreamUtil = TcpStreamUtil(config: tcpStreamUtilConfig)
            (status, resultResponse) = tcpStreamUtil.runStream()
        case .udp:
            let udpStreamUtilConfig = UdpStreamUtilConfiguration(
                host: host,
                port: port,
                outgoing: true,
                timeoutNs: timeoutNs,
                delayNs: delayNs,
                packetCount: packetCount,
                uuid: nil,
                payload: payload
            )

            let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
            let (streamUtilStatus, result) = udpStreamUtil.runStream()

            status = streamUtilStatus
            resultResponse = result?.receivedPayload
            rttNs = result?.rttsNs?.values.first
        }
    }
}
