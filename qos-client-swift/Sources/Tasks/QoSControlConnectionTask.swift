import Foundation

class QoSControlConnectionTask: QoSTask {
    
    let controlConnectionParams: ControlConnectionParameters

    var controlConnection: ControlConnection?

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys2.self)

        let host = try container.decode(String.self, forKey: .host)
        
        var optionalPort = try? container.decodeIfPresent(UInt16.self, forKey: .port)
        if optionalPort == nil {
            if let portString = try? container.decodeIfPresent(String.self, forKey: .port) {
                optionalPort = UInt16(portString)
            }
        }
        
        guard let port = optionalPort else {
            throw ParseError.parseError("\"server_port\" could not be unmarshalled.")
        }

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

    func executeCommandAndAwaitOk(cmd: String) -> Bool {
        do {
            let response = try executeCommand(cmd: cmd, waitForAnswer: true)

            return response?.starts(with: "OK") ?? false
        } catch {
            return false
        }
    }

    func executeCommand(cmd: String, waitForAnswer: Bool = false) throws -> String? {
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

        _ = semaphore.wait(timeout: DispatchTime.distantFuture)

        if let err = error {
            throw err
        }

        return response
    }
}
