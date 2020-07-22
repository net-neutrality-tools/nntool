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

import XCTest
@testable import QoSKit
import CodableJSON

///
class QoSKitUnitTests: XCTestCase {

    func testWebsite() {
        let t = WebsiteRenderingTask.create(config: [
            "qos_test_uid": "1",
            "qostest": "WEBSITE",
            "concurrency_group": 200,
            "url": "https://alladin.at"
        ])!

        runTaskAndLogResult(t)
    }

    func testTraceroute() {
        let t = TracerouteTask.create(config: [
            "qos_test_uid": "1",
            "concurrency_group": 200,
            "qostest": "TRACEROUTE",
            "host": JSON("alladin.at"),
            "max_hops": JSON(20)
        ])

        runTaskAndLogResult(t)
    }

    func testTracerouteReverse() {
        let t = TracerouteTask.create(config: [
            "qos_test_uid": "1",
            "concurrency_group": 200,
            "qostest": "TRACEROUTE",
            "host": JSON("https://peer-ias-de-01.net-neutrality.tools:8443/"),
            "is_reverse": JSON(true),
            "max_hops": JSON(20),
            "timeout": JSON(20 * NSEC_PER_SEC)
        ])

        runTaskAndLogResult(t)
    }

    func testMeasurementKitWebConnectivity() {
        let t = MeasurementKitTask.create(config: [
            "qos_test_uid": JSON("1"),
            "concurrency_group": JSON(200),
            "qostest": JSON("MKIT_WEB_CONNECTIVITY"),
            "input": JSON("[\"https://alladin.at\"]")
        ])

        runTaskAndLogResult(t)
    }

    func testMeasurementKitDash() {
        let t = MeasurementKitTask.create(config: [
            "qos_test_uid": JSON("2"),
            "concurrency_group": JSON(201),
            "qostest": JSON("MKIT_DASH")
        ])

        runTaskAndLogResult(t)
    }

    func testSip() {
        let config: QoSTaskConfiguration = [
            "qos_test_uid": "1",
            "concurrency_group": 200,
            "qostest": "SIP",

            "server_addr": "localhost",
            "server_port": 5233,

            "port": 5060,
            "count": 3,
            "call_duration": JSON(5 * NSEC_PER_SEC),
            "to": "abc",
            "from": "def",
            "via": "ghi"
        ]

        let t = SipTask.create(config: config)

        t?.controlConnection = mockedControlConnection(config)

        runTaskAndLogResult(t)
    }

    func testVoip() {
        let config: QoSTaskConfiguration = [
            "qos_test_uid": "1",
            "concurrency_group": 200,
            "qostest": "VOIP",

            "server_addr": "localhost",
            "server_port": 5233,

            "out_port": 5060,
            "in_port": 50601
        ]

        let t = VoipTask.create(config: config)

        t?.controlConnection = mockedControlConnection(config)

        runTaskAndLogResult(t)
    }

    func testUdpOut() {
        let config: QoSTaskConfiguration = [
            "qos_test_uid": "1",
            "concurrency_group": 200,
            "qostest": "UDP",

            "server_addr": "localhost",
            "server_port": 5233,

            "out_port": 10245,
            "out_num_packets": 10
        ]

        let t = UdpPortTask.create(config: config)

        t?.controlConnection = mockedControlConnection(config)

        runTaskAndLogResult(t)
    }

    func testUdpIn() {
        let config: QoSTaskConfiguration = [
            "qos_test_uid": "1",
            "concurrency_group": 200,
            "qostest": "UDP",

            "server_addr": "localhost",
            "server_port": 5233,

            "in_port": 50000,
            "in_num_packets": 10
        ]

        let t = UdpPortTask.create(config: config)

        t?.controlConnection = mockedControlConnection(config)

        runTaskAndLogResult(t)
    }

    func testEchoProtocol() {
        let t = EchoProtocolTask.create(config: [
            "concurrency_group": 301,
            "qos_test_uid": "301",
            "qostest": "echo_protocol",
            "protocol": "udp",
            "payload": "__payload__",
            "host": "localhost",
            "port": "7"
        ])

        runTaskAndLogResult(t)
    }

    private func mockedControlConnection(_ config: QoSTaskConfiguration) -> ControlConnection {
        return ControlConnection(
            host: config["server_addr"]?.stringValue ?? "localhost",
            port: config["server_port"]?.uint16Value ?? 5233,
            timeoutS: 15,
            token: "\(UUID().uuidString.lowercased())_\(UInt(Date().timeIntervalSince1970))_abc"
        )
    }

    private func runTaskAndLogResult(_ task: QoSTask?) {
        if let result = task?.runTask() {
            do {
                let encoder = JSONEncoder()
                encoder.outputFormatting = .prettyPrinted

                let d = try encoder.encode(result)
                logger.debug(String(data: d, encoding: .utf8))
            } catch {
                logger.debug("err")
            }
        }
    }
}

struct AnyKey: CodingKey {
    var stringValue: String
    var intValue: Int?

    init?(stringValue: String) {
        self.stringValue = stringValue
        self.intValue = nil
    }

    init?(intValue: Int) {
        self.stringValue = String(intValue)
        self.intValue = intValue
    }
}
