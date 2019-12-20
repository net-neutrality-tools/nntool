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
import nntool_shared_swift

class EchoProtocolTask: QoSTask {

    public enum ProtocolType: String, Codable {
        case tcp // = "tcp"
        case udp // = "udp"
    }

    private var host: String
    private var port: UInt16 = 7
    private var protocolType: ProtocolType = .udp
    private var payload: String

    private let packetCount = 1 // TODO: config (echo protocol test currently only works with one packet)
    private let delayNs = 1 * NSEC_PER_SEC

    ///
    enum CodingKeys4: String, CodingKey {
        case host
        case port
        case protocolType = "protocol"
        case payload
    }

    private var resultResponse: String?
    private var rttNs: UInt64?

    private var udpPacketSentTimestamp: UInt64?

    override var statusKey: String {
        return "echo_protocol_status"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["echo_protocol_objective_host"] = JSON(host)
        r["echo_protocol_objective_port"] = JSON(port)
        r["echo_protocol_objective_protocol"] = JSON(protocolType.rawValue)
        r["echo_protocol_objective_payload"] = JSON(payload)

        r["echo_protocol_result"] = JSON(resultResponse)
        r["echo_protocol_result_rtt_ns"] = JSON(rttNs)

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        host = try container.decode(String.self, forKey: .host)
        payload = try container.decode(String.self, forKey: .payload)

        if let serverPort = container.decodeIfPresentWithStringFallback(UInt16.self, forKey: .port) {
            port = serverPort
        }

        if let pType = try container.decodeIfPresent(ProtocolType.self, forKey: .protocolType) {
            protocolType = pType
        }

        try super.init(from: decoder)
    }

    ///
    override public func taskMain() {
        switch protocolType {
        case .tcp:
            let tcpStreamUtilConfig = TcpStreamUtilConfiguration(
                host: host,
                port: port,
                outgoing: true,
                timeoutNs: timeoutNs,
                payload: payload
            )

            let tcpStreamUtil = TcpStreamUtil(config: tcpStreamUtilConfig)
            (status, resultResponse) = tcpStreamUtil.runStream()
        case .udp:
            let udpStreamUtilConfig = UdpStreamUtilConfiguration(
                host: host,
                portOut: port,
                portIn: nil,
                respondOnly: false,
                timeoutNs: timeoutNs,
                delayNs: delayNs,
                packetCount: packetCount
            )

            let udpStreamUtil = UdpStreamUtil(config: udpStreamUtilConfig)
            udpStreamUtil.delegate = self

            status = udpStreamUtil.runStream()
        }
    }
}

extension EchoProtocolTask: UdpStreamUtilDelegate {

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didBindToLocalPort port: UInt16) {
        // do nothing
    }

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, willSendPacketWithNumer packetNum: Int) -> (Data, UdpStreamUtil.Tag)? {
        taskLogger.debug("ON SEND (packet: \(packetNum))")

        guard let data = payload.data(using: .utf8) else {
            return nil
        }

        udpPacketSentTimestamp = TimeHelper.currentTimeNs()

        return (data, .noDelay)
    }

    func udpStreamUtil(_ udpStreamUtil: UdpStreamUtil, didReceiveData data: Data, fromAddress address: Data, atTimestamp timestamp: UInt64) -> Bool {
        resultResponse = String(data: data, encoding: .utf8)

        if let sentTs = udpPacketSentTimestamp {
            rttNs = timestamp - sentTs
        }

        return true
    }
}
