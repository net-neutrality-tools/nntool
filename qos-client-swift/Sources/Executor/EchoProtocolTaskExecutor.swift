/***************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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
public class EchoProtocolTaskExecutor: AbstractTaskExecutor<EchoProtocolTaskConfiguration, EchoProtocolTaskResult>, GCDAsyncSocketDelegate {

    ///
    private var resultResponse: String?

    ///
    override var taskType: TaskType? {
        return .echoProtocol
    }

    ///
    public override var result: EchoProtocolTaskResult {
        let r = super.result

        r.objectiveHost = internalConfig.host
        r.objectivePort = internalConfig.port
        r.objectiveProtocolType = internalConfig.protocolType
        r.objectivePayload = internalConfig.payload

        r.result = resultResponse

        return r
    }

    ///
    override public func main() {
        guard
            let host = internalConfig.host,
            let port = internalConfig.port,
            let protocolType = internalConfig.protocolType,
            let payload = internalConfig.payload
        else {
            self.status = .error
            return
        }

        switch protocolType {
        case .tcp:
            let tcpStreamUtilConfig = TcpStreamUtilConfiguration(
                host: host,
                port: port,
                outgoing: true,
                timeoutNs: internalConfig.timeoutNs,
                payload: payload
            )

            let tcpStreamUtil = TcpStreamUtil(config: tcpStreamUtilConfig)
            (status, resultResponse) = tcpStreamUtil.runStream()
        case .udp:
            let udpStreamUtilConfig = UdpStreamUtilConfiguration(
                host: host,
                port: port,
                outgoing: true,
                timeoutNs: internalConfig.timeoutNs,
                delayNs: 1 * NSEC_PER_SEC, // TODO: config
                packetCount: 1/*packetCount*/, // TODO: config (echo protocol test currently only works with one packet)
                uuid: nil,
                payload: payload
            )

            let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
            let (streamUtilStatus, result) = udpStreamUtil.runStream()

            status = streamUtilStatus
            resultResponse = result?.receivedPayload
        }
    }
}
