import Foundation
import CodableJSON

class EchoProtocolTask: QoSTask {

    public enum ProtocolType: String {
        case tcp // = "tcp"
        case udp // = "udp"
    }

    var host: String
    var port: UInt16 = 7
    var protocolType: ProtocolType = .udp
    var payload: String

    // packetCount?

    ///
    enum CodingKeys4: String, CodingKey {
        case host
        case port
        case protocolType = "protocol"
        case payload
    }

    private var resultResponse: String?

    override var statusKey: String {
        return "echo_protocol_status"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["echo_protocol_result"] = JSON(resultResponse)

        return r
    }

    ///
    override init?(config: QoSTaskConfiguration) {
        guard let host = config[CodingKeys4.host.rawValue]?.stringValue else {
            logger.debug("host nil")
            return nil
        }

        guard let payload = config[CodingKeys4.payload.rawValue]?.stringValue else {
            logger.debug("payload nil")
            return nil
        }

        if let port = config[CodingKeys4.port.rawValue]?.uint16Value {
            self.port = port
        }

        if let protocolTypeString = config[CodingKeys4.protocolType.rawValue]?.stringValue, let protocolType = ProtocolType(rawValue: protocolTypeString) {
            self.protocolType = protocolType
        }

        self.host = host
        self.payload = payload

        super.init(config: config)
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        host = try container.decode(String.self, forKey: .host)
        payload = try container.decode(String.self, forKey: .payload)

        if let portString = try container.decodeIfPresent(String.self, forKey: .port), let port = UInt16(portString) {
            self.port = port
        }

        if let protocolTypeString = try container.decodeIfPresent(String.self, forKey: .protocolType), let protocolType = ProtocolType(rawValue: protocolTypeString) {
            self.protocolType = protocolType
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
            let (tcpUtilstatus, resultResponse) = tcpStreamUtil.runStream()

            self.status = QoSTaskStatus(rawValue: tcpUtilstatus.rawValue) ?? .error
            self.resultResponse = resultResponse
        case .udp:
            let udpStreamUtilConfig = UdpStreamUtilConfiguration(
                host: host,
                port: port,
                outgoing: true,
                timeoutNs: timeoutNs,
                delayNs: 1 * NSEC_PER_SEC, // TODO: config
                packetCount: 1/*packetCount*/, // TODO: config (echo protocol test currently only works with one packet)
                uuid: nil,
                payload: payload
            )

            let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
            let (streamUtilStatus, result) = udpStreamUtil.runStream()

            status = QoSTaskStatus(rawValue: streamUtilStatus.rawValue) ?? .error
            resultResponse = result?.receivedPayload
        }
    }
}
