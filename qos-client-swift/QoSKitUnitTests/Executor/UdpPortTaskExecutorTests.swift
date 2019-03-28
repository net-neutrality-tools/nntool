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
import ObjectMapper

class UdpPortQoSTaskExecutorTests: XCTestCase {

    override func setUp() {
        continueAfterFailure = false
    }

    func testOutgoing() {
        // TODO: mock control connection
        // TODO: mock cocoaasyncsocket

        let config = UdpPortTaskConfigurationBuilder()
            .timeoutNs(10 * NSEC_PER_SEC)
            .serverAddress(/*"localhost"*/"peer-qos-de-01.net-neutrality.tools")
            .serverPort(5233)
            .portOut(5060)
            .qosTestUid(12)
            .packetCountOut(5)
            .delayNs(20 * NSEC_PER_MSEC)
            .build()

        let controlConnection = ControlConnection(
            host: config.serverAddress!,
            port: config.serverPort!,
            tls: true,
            timeoutS: config.timeoutS,
            token: "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw="
        )

        let executor = UdpPortTaskExecutor(config: config, controlConnection: controlConnection)
        let result = executor.runTask()

        controlConnection.disconnect()

        print(result.toJSONString(prettyPrint: true)!)
    }

    func testIncoming() {
        // TODO: mock control connection
        // TODO: mock cocoaasyncsocket

        let config = UdpPortTaskConfigurationBuilder()
            .timeoutNs(10 * NSEC_PER_SEC)
            .serverAddress("localhost")
            .serverPort(5233)
            .portIn(5061)
            .qosTestUid(13)
            .packetCountIn(5)
            .delayNs(20 * NSEC_PER_MSEC)
            .build()

        let controlConnection = ControlConnection(
            host: config.serverAddress!,
            port: config.serverPort!,
            tls: true,
            timeoutS: config.timeoutS,
            token: "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw="
        )

        let executor = UdpPortTaskExecutor(config: config, controlConnection: controlConnection)
        let result = executor.runTask()

        controlConnection.disconnect()

        print(result.toJSONString(prettyPrint: true)!)
    }
}
