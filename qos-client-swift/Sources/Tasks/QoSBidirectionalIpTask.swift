import Foundation

class QoSBidirectionalIpTask: QoSControlConnectionTask {

    ///
    enum Direction: String {
        case unknown = "UNKNOWN"
        case outgoing = "OUT"
        case incoming = "IN"
    }

    ///
    var portOut: UInt16?

    //
    var portIn: UInt16?

    ///
    var direction: Direction {
        if let p = portOut, p > 0 {
            return .outgoing
        }

        if let p = portIn, p > 0 {
            return .incoming
        }

        return .unknown
    }

    ///
    override init?(config: QoSTaskConfiguration) {
        logger.debug(config)

        if let portOut = config[CodingKeys3.portOut.rawValue]?.uint16Value {
            self.portOut = portOut
        }

        if let portIn = config[CodingKeys3.portIn.rawValue]?.uint16Value {
            self.portIn = portIn
        }

        super.init(config: config)
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys3.self)

        if let portOutString = try container.decodeIfPresent(String.self, forKey: .portOut) {
            portOut = UInt16(portOutString)
        }

        if let portInString = try container.decodeIfPresent(String.self, forKey: .portIn) {
            portIn = UInt16(portInString)
        }

        try super.init(from: decoder)
    }

    ///
    enum CodingKeys3: String, CodingKey {
        case portOut = "out_port"
        case portIn = "in_port"
    }
}
