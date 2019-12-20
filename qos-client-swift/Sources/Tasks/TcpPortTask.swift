/***************************************************************************
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

class TcpPortTask: QoSBidirectionalIpTask {

    ///
    static let PAYLOAD = "PING"

    private var resultResponse: String?

    override var statusKey: String? {
        return direction == .outgoing ? "tcp_result_out" : "tcp_result_in"
    }

    override var objectiveTimeoutKey: String? {
        return "tcp_objective_timeout"
    }

    override var result: QoSTaskResult {
        var r = super.result

        // TODO: r["tcp_result_error_details"] = "?"

        switch direction {
        case .outgoing:
            r["tcp_objective_out_port"] = JSON(portOut)

            r["tcp_result_out_response"] = JSON(resultResponse)

        case .incoming:
            r["tcp_objective_in_port"] = JSON(portIn)

            r["tcp_result_in_response"] = JSON(resultResponse)

        default:
            break
        }

        return r
    }

    ///
    override public func taskMain() {
        guard let tcpStreamUtilConfig = currentTcpStreamUtilConfiguration() else {
            logger.error("tcpStreamUtilConfig could not be generated")
            self.status = .error
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

        taskLogger.info("TCP port task finished")
    }

    ///
    private func currentTcpStreamUtilConfiguration() -> TcpStreamUtilConfiguration? {
        var tcpStreamUtilConfig = TcpStreamUtilConfiguration(
            host: controlConnectionParams.host,
            port: 0, // gets set in switch/case
            outgoing: true,
            timeoutNs: timeoutNs,
            payload: TcpPortTask.PAYLOAD
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
