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
import CodableJSON
import CocoaAsyncSocket

class NonTransparentProxyTask: QoSControlConnectionTask {

    private var request: String
    private var port: UInt16

    private var semaphore: DispatchSemaphore?

    private var socketResult: String?

    ///
    enum CodingKeys4: String, CodingKey {
        case request
        case port
    }

    override var statusKey: String? {
        return "nontransproxy_result"
    }

    override var objectiveTimeoutKey: String? {
        return "nontransproxy_objective_timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["nontransproxy_objective_request"] = JSON(request)
        r["nontransproxy_objective_port"] = JSON(port)

        r["nontransproxy_result_response"] = JSON(socketResult)

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        request = try container.decode(String.self, forKey: .request)

        port = try container.decodeWithStringFallback(UInt16.self, forKey: .port)

        try super.init(from: decoder)
    }

    ///
    override public func taskMain() {
        let cmd = String(format: "NTPTEST %lu +ID%d", port, uid)

        guard executeCommandAndAwaitOk(cmd: cmd) else {
            status = .error
            return
        }

        let delegateQueue = DispatchQueue(label: "eu.nntool.ios.qos.nontransparentproxy.delegate")
        let socket = GCDAsyncSocket(delegate: self, delegateQueue: delegateQueue)

        do {
            try socket.connect(toHost: controlConnectionParams.host, onPort: controlConnectionParams.port)
        } catch {
            status = .error
            return
        }

        semaphore = DispatchSemaphore(value: 0)
        let semaphoreResult = semaphore?.wait(timeout: .now() + .nanoseconds(Int(timeoutNs)))

        socket.disconnect()

        if status == .unknown {
            if semaphoreResult == .timedOut {
                status = .timeout
            } else {
                if socketResult != nil {
                    status = .ok
                } else {
                    status = .error
                }
            }
        }
    }
}

extension NonTransparentProxyTask: GCDAsyncSocketDelegate {

    func socket(_ sock: GCDAsyncSocket, didConnectToHost host: String, port: UInt16) {
        if let sendData = "\(request)\n".data(using: .utf8) {
            sock.write(sendData, withTimeout: TimeInterval(timeoutS), tag: 1)
        } else {
            status = .error
        }
    }

    func socketDidDisconnect(_ sock: GCDAsyncSocket, withError err: Error?) {
        semaphore?.signal()
    }

    func socket(_ sock: GCDAsyncSocket, didWriteDataWithTag tag: Int) {
        sock.readData(to: "\n".data(using: .utf8)!, withTimeout: TimeInterval(timeoutS), tag: 1)
    }

    func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int) {
        if let line = String(data: data, encoding: .utf8) {
            socketResult = line.trimmingCharacters(in: .whitespacesAndNewlines)
        }

        semaphore?.signal()
    }
}
