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
import CocoaAsyncSocket

///
struct TcpStreamUtilConfiguration {

    var host: String
    var port: UInt16
    var outgoing: Bool
    var timeoutNs: UInt64
    var payload: String

    var timeoutS: Double {
        return Double(timeoutNs / NSEC_PER_SEC)
    }
}

///
class TcpStreamUtil: NSObject {

    static let TAG = 0
    static let backslashNData = "\n".data(using: .utf8)!

    private var semaphore: DispatchSemaphore?

    /// This variable is used to retain the socket returned by didAcceptNewSocket delegate function,
    /// otherwise it would be released and the connection would be closed.
    private var incomingSocket: GCDAsyncSocket?

    private let config: TcpStreamUtilConfiguration

    private var result: String?
    private var status: QoSTaskStatus = .unknown

    var controlFunc: (() -> (Bool))?

    init(config: TcpStreamUtilConfiguration) {
        self.config = config
    }

    ///
    public func runStream() -> (QoSTaskStatus, String?) {
        let delegateQueue = DispatchQueue(label: "at.alladin.nettest.qos.tcpstream.delegate")
        let socket = GCDAsyncSocket(delegate: self, delegateQueue: delegateQueue)

        do {
            if config.outgoing {
                // outgoing -> controlFunc before connect
                try evaluateControlFunc()

                try socket.connect(toHost: config.host, onPort: config.port, withTimeout: config.timeoutS)
            } else {
                try socket.accept(onPort: config.port)

                // outgoing -> controlFunc after accept
                try evaluateControlFunc()
            }
        } catch {
            if config.outgoing {
                logger.error("Could not connect to host \(config.host) on port \(config.port)")
            } else {
                logger.error("Could not accept on port \(config.port)")
            }

            return (.error, nil)
        }

        semaphore = DispatchSemaphore(value: 0)
        let timeoutResult = semaphore?.wait(timeout: .now() + .nanoseconds(Int(config.timeoutNs)))

        if timeoutResult == .timedOut {
            status = .timeout
        } else if let r = result, !r.isEmpty {
            status = .ok
        } else {
            status = .error
        }

        socket.delegate = nil
        socket.disconnect()

        return (status, result)
    }

    ///
    private func evaluateControlFunc() throws {
        if let c = controlFunc, !c() {
            throw TaskError.controlConnectionError
        }
    }
}

extension TcpStreamUtil: GCDAsyncSocketDelegate {

    // MARK: GCDAsyncSocketDelegate (outgoing)

    func socket(_ sock: GCDAsyncSocket, didConnectToHost host: String, port: UInt16) {
        var payload = config.payload

        payload.append("\n")
        guard let payloadData = payload.data(using: .utf8) else {
            return
        }

        //logger.debug("writing payload: \(payload)")

        sock.write(payloadData, withTimeout: config.timeoutS, tag: TcpStreamUtil.TAG)
    }

    func socket(_ sock: GCDAsyncSocket, didWriteDataWithTag tag: Int) {
        sock.readData(to: TcpStreamUtil.backslashNData, withTimeout: config.timeoutS, tag: TcpStreamUtil.TAG)
    }

    // MARK: GCDAsyncSocketDelegate (incoming)

    func socket(_ sock: GCDAsyncSocket, didAcceptNewSocket newSocket: GCDAsyncSocket) {
        incomingSocket = newSocket // retain socket

        newSocket.readData(to: TcpStreamUtil.backslashNData, withTimeout: config.timeoutS, tag: TcpStreamUtil.TAG)
    }

    // MARK: GCDAsyncSocketDelegate

    func socket(_ sock: GCDAsyncSocket, didRead data: Data, withTag tag: Int) {
        // TODO: assert tag TcpStreamUtil.TAG

        let response = String(data: data, encoding: .utf8)
        result = response?.trimmingCharacters(in: .whitespacesAndNewlines)

        sock.disconnect()
    }

    func socketDidDisconnect(_ sock: GCDAsyncSocket, withError err: Error?) {
        semaphore?.signal()
    }
}
