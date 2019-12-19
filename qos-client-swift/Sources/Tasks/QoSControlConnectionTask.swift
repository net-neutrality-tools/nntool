/***************************************************************************
 * Copyright 2017 appscape gmbh
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
 ***************************************************************************/

import Foundation

class QoSControlConnectionTask: QoSTask {

    let controlConnectionParams: ControlConnectionParameters

    var controlConnection: ControlConnection?

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys2.self)

        let host = try container.decode(String.self, forKey: .host)

        let serverPort = try container.decodeWithStringFallback(UInt16.self, forKey: .port)

        controlConnectionParams = ControlConnectionParameters(host: host, port: serverPort)

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
