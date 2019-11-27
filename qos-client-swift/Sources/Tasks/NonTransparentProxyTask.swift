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

        var serverPort = try? container.decodeIfPresent(UInt16.self, forKey: .port)
        if serverPort == nil {
            if let serverPortString = try? container.decodeIfPresent(String.self, forKey: .port) {
                serverPort = UInt16(serverPortString)
            }
        }

        guard let sp = serverPort else {
            throw ParseError.parseError("Could not parse port")
        }

        port = sp

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
