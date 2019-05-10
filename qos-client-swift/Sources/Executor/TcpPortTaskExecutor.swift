// TODO: license

import Foundation
import XCGLogger

///
public class TcpPortTaskExecutor: AbstractBidirectionalIpTaskExecutor<TcpPortTaskConfiguration, TcpPortTaskResult> {

    ///
    static let PAYLOAD = "PING"

    private var resultResponse: String?

    override var taskType: TaskType? {
        return .tcpPort
    }

    ///
    override public var result: TcpPortTaskResult {
        let r = super.result

        switch internalConfig.direction {
        case .outgoing:
            r.resultOut = status
            r.resultOutResponse = resultResponse

        case .incoming:
            r.resultIn = status
            r.resultInResponse = resultResponse

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

        guard let qosTestUid = internalConfig.qosTestUid else {
            logger.warning("qosTestUid is not set")
            self.status = .error
            return
        }

        // run tcp stream util
        let tcpStreamUtil = TcpStreamUtil(config: tcpStreamUtilConfig)
        tcpStreamUtil.controlFunc = {
            // control connection request
            let cmd = String(format: "TCPTEST %@ %lu +ID%d", self.internalConfig.direction.rawValue, tcpStreamUtilConfig.port, qosTestUid)

            do {
                let waitForAnswer = self.internalConfig.direction == .outgoing

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

        (status, resultResponse) = tcpStreamUtil.runStream()

        // TODO
        //internalResult?.resultErrorDetails = "TODO"

        logger.info("TCP port task finished")
    }

    ///
    private func currentTcpStreamUtilConfiguration() -> TcpStreamUtilConfiguration? {
        guard let host = internalConfig.serverAddress else {
            return nil
        }

        var tcpStreamUtilConfig = TcpStreamUtilConfiguration(
            host: host,
            port: 0, // gets set in switch/case
            outgoing: true,
            timeoutNs: internalConfig.timeoutNs,
            payload: TcpPortTaskExecutor.PAYLOAD
        )

        switch internalConfig.direction {
        case .outgoing:
            guard let portOut = internalConfig.portOut else {
                return nil
            }

            tcpStreamUtilConfig.port = portOut
            tcpStreamUtilConfig.outgoing = true

        case .incoming:
            guard let portIn = internalConfig.portIn else {
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
