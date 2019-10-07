import Foundation

class SipMessage {
    static let SIP_VERSION = "2.0"
    static let SIP_PROTOCOL_STRING = "SIP/" + SipMessage.SIP_VERSION

    private(set) var headers = [String: String]()

    let from: String
    let to: String
    let via: String?

    var body: Data?

    var data: Data {
        var data = Data()

        writeString(data: &data, str: "\(getFirstLine())\n")

        writeHeader(data: &data, name: "FROM", value: from)
        writeHeader(data: &data, name: "TO", value: to)
        writeHeader(data: &data, name: "VIA", value: via != nil ? via! : from)

        headers.forEach { writeHeader(data: &data, name: $0, value: $1) }

        if let b = body {
            writeString(data: &data, str: "Content-Length: \(b.count)\n\n")
            data.append(b)
        } else {
            writeString(data: &data, str: "Content-Length: 0\n\n")
        }

        return data
    }

    init(from: String, to: String, via: String? = nil, body: Data? = nil) {
        self.from = from
        self.to = to
        self.via = via
    }

    func getFirstLine() -> String {
        return ""
    }

    func setHeader(name: String, value: String) {
        headers[name] = value
    }

    private func writeHeader(data: inout Data, name: String, value: String) {
        writeString(data: &data, str: "\(name): \(value)\n")
    }

    private func writeString(data: inout Data, str: String) {
        if let strData = str.data(using: .utf8) {
            data.append(strData)
        }
    }
}

class SipRequestMessage: SipMessage {

    enum RequestType: String {
        case invite = "INVITE"
        case ack = "ACK"
        case bye = "BYE"
    }

    var type: RequestType

    init(type: RequestType, from: String, to: String, via: String? = nil, body: Data? = nil) {
        self.type = type

        super.init(from: from, to: to, via: via, body: body)
    }

    override func getFirstLine() -> String {
        return "\(type.rawValue) \(to) \(SipRequestMessage.SIP_PROTOCOL_STRING)"
    }
}

class SipResponseMessage: SipMessage {

    enum ResponseType: Int, CustomStringConvertible {
        case trying = 100
        case ringing = 180
        case ok = 200

        var description: String {
            switch self {
            case .trying: return "TRYING"
            case .ringing: return "RINGING"
            case .ok: return "OK"
            }
        }
    }

    let type: ResponseType

    convenience init?(data: Data) {
        guard data.count > 0 else {
            return nil
        }

        let parts = data.split(separator: Character("\n").asciiValue!)

        guard parts.count > 0 else {
            return nil
        }

        //logger.debug(parts.map { String(data: $0, encoding: .utf8)! })

        guard let firstLine = String(data: parts[0], encoding: .utf8) else {
            return nil
        }

        let firstLineParts = firstLine.split(separator: " ")

        guard firstLineParts.count > 0 else {
            return nil
        }

        guard let code = Int(String(firstLineParts[1])) else {
            return nil
        }

        guard let type = ResponseType(rawValue: code), type.description == String(firstLineParts[2]) else {
            return nil
        }

        var from: String?
        var to: String?
        var via: String?

        var headers = [String: String]()

        for i in 1..<parts.count {
            guard let partStr = String(data: parts[i], encoding: .utf8) else {
                continue
            }

            let headerParts = partStr.split(separator: ":")

            guard headerParts.count > 1 else {
                continue
            }

            let name = headerParts[0].trimmingCharacters(in: .whitespaces)
            let value = headerParts[1].trimmingCharacters(in: .whitespaces)

            switch name {
            case "FROM": from = value
            case "TO": to = value
            case "VIA": via = value
            default:
                headers[name] = value
            }
        }

        guard let f = from, let t = to else {
            return nil
        }

        self.init(type: type, from: f, to: t, via: via)

        headers.forEach { setHeader(name: $0, value: $1) }
    }

    init(type: ResponseType, from: String, to: String, via: String? = nil, body: Data? = nil) {
        self.type = type

        super.init(from: from, to: to, via: via, body: body)
    }
}
