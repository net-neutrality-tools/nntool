import Foundation

class TcpPortTask: QoSBidirectionalIpTask {

    ///
    static let PAYLOAD = "PING"

    private var resultResponse: String?

    override var statusKey: String {
        return direction == .outgoing ? "tcp_result_out" : "tcp_result_in"
    }

    override var result: QoSTaskResult {
        var r = super.result

        // TODO: r["tcp_result_error_details"] = "?"
        // TODO: set objective values?

        switch direction {
        case .outgoing:
            r["tcp_result_out_response"] = resultResponse

        case .incoming:
            r["tcp_result_in_response"] = resultResponse

        default:
            break
        }

        return r
    }

    ///
    override public func main() {
        guard let tcpStreamUtilConfig = currentTcpStreamUtilConfiguration() else {
            self.status = .error
            logger.warning("tcpStreamUtilConfig could not be generated")
            return
        }

        // run tcp stream util
        let tcpStreamUtil = TcpStreamUtil(config: tcpStreamUtilConfig)
        tcpStreamUtil.controlFunc = {
            // control connection request
            let cmd = String(format: "TCPTEST %@ %lu +ID%d", self.direction.rawValue, tcpStreamUtilConfig.port, self.uid)

            do {
                let waitForAnswer = self.direction == .outgoing

                let response = try self.executeCommand(cmd: cmd, waitForAnswer: waitForAnswer)

                if waitForAnswer {
                    guard let r = response, r.starts(with: "OK") else {
                        return false
                    }
                }
            } catch {
                return false
            }

            return true
        }

        let (utilStatus, resultResponse) = tcpStreamUtil.runStream()

        self.status = QoSTaskStatus(rawValue: utilStatus.rawValue) ?? .error
        self.resultResponse = resultResponse

        // TODO
        //internalResult?.resultErrorDetails = "?"

        logger.info("TCP port task finished")
    }

    ///
    private func currentTcpStreamUtilConfiguration() -> TcpStreamUtilConfiguration? {
        var tcpStreamUtilConfig = TcpStreamUtilConfiguration(
            host: controlConnectionParams.host,
            port: 0, // gets set in switch/case
            outgoing: true,
            timeoutNs: timeoutNs,
            payload: TcpPortTaskExecutor.PAYLOAD
        )

        switch direction {
        case .outgoing:
            guard let portOut = portOut else {
                return nil
            }

            tcpStreamUtilConfig.port = portOut
            tcpStreamUtilConfig.outgoing = true

        case .incoming:
            guard let portIn = portIn else {
                return nil
            }

            tcpStreamUtilConfig.port = portIn
            tcpStreamUtilConfig.outgoing = false

        default:
            return nil
        }

        return tcpStreamUtilConfig
    }
}