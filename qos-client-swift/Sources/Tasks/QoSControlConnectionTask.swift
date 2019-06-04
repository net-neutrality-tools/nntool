import Foundation

class QoSControlConnectionTask: QoSTask {

    let controlConnectionParams: ControlConnectionParameters

    var controlConnection: ControlConnection?

    ///
    override init?(config: QoSTaskConfiguration) {
        logger.debug(config)

        guard let host = config[CodingKeys2.host.rawValue]?.stringValue, !host.isEmpty else {
            logger.debug("host nil")
            return nil
        }

        guard let portStr = config[CodingKeys2.port.rawValue]?.stringValue, let port = UInt16(portStr), port > 0 else {
            logger.debug("port nil")
            return nil
        }

        /*guard let port = config[CodingKeys2.port.rawValue]?.uint16Value, port > 0 else {
            logger.debug("port nil")
            return nil
        }*/

        controlConnectionParams = ControlConnectionParameters(host: host, port: port)

        super.init(config: config)
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys2.self)

        let host = try container.decode(String.self, forKey: .host)
        let port = try container.decode(UInt16.self, forKey: .port)

        controlConnectionParams = ControlConnectionParameters(host: host, port: port)

        try super.init(from: decoder)
    }

    ///
    enum CodingKeys2: String, CodingKey {
        case host = "server_addr"
        case port = "server_port"
    }

    ///
    func extractUuidFromToken() -> String {
        return controlConnection?.token.components(separatedBy: "_").first ?? "ERR" // TODO
    }
}

// MARK: - RequireControlConnection

extension QoSControlConnectionTask: RequireControlConnection {

    func executeCommand(cmd: String, waitForAnswer: Bool = false, timeoutNs: UInt64 = 5 * NSEC_PER_SEC) throws -> String? {
        guard let cc = controlConnection else {
            //throw TODO: error
            return nil
        }

        let semaphore = DispatchSemaphore(value: 0)

        var response: String?
        var error: Error?

        cc.executeCommand(cmd: cmd, waitForAnswer: waitForAnswer, success: { r in
            response = r

            semaphore.signal()
        }, error: { err in
            error = err

            semaphore.signal()
        })

        let semaphoreResult = semaphore.wait(timeout: .now() + .nanoseconds(Int(timeoutNs)))
        if semaphoreResult == .timedOut {
            //throw TODO: timeout
            return nil
        }

        if let err = error {
            throw err
        }

        return response
    }
}
