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
import Nimble
@testable import QoSKit

class TcpPortTaskConfigurationTests: XCTestCase {

    override func setUp() {
        continueAfterFailure = false
    }
    
    func testValidJsonObjectMapping() {
        let serverAddress = "test.host"
        let serverPort: UInt16 = 443
        
        let timeoutNs: UInt64 = 30 * 1_000_000_000
        
        let concurrencyGroup = 10
        let qosTestUid = 1
        
        let portOut: UInt16 = 8080
        let portIn: UInt16 = 8444
        
        let jsonString = """
        {
            "server_addr": "\(serverAddress)",
            "server_port": \(serverPort),
            "timeout": \(timeoutNs),
            "concurrency_group": \(concurrencyGroup),
            "qos_test_uid": \(qosTestUid),
            "out_port": \(portOut),
            "in_port": \(portIn),
        }
        """
        
        let config = TcpPortTaskConfiguration(JSONString: jsonString)
        
        expect(config).notTo(beNil())
        
        expect(config?.serverAddress).notTo(beNil())
        expect(config?.serverPort).notTo(beNil())
        expect(config?.timeoutNs).notTo(beNil())
        expect(config?.concurrencyGroup).notTo(beNil())
        expect(config?.qosTestUid).notTo(beNil())
        expect(config?.portOut).notTo(beNil())
        //XCTAssertNotNil(config!.portIn)
        
        ////
        
        expect(config?.serverAddress).to(equal(serverAddress))
        expect(config?.serverPort).to(equal(serverPort))
        expect(config?.timeoutNs).to(equal(timeoutNs))
        expect(config?.concurrencyGroup).to(equal(concurrencyGroup))
        expect(config?.qosTestUid).to(equal(qosTestUid))
        expect(config?.portOut).to(equal(portOut))
        //XCTAssertEqual(portIn, config?.portIn)
    }
    
    func testInvalidPortJsonObjectMapping() {
        let jsonString = """
        {
            "out_port": 1000000
        }
        """
        
        let config = TcpPortTaskConfiguration(JSONString: jsonString)
        
        expect(config).notTo(beNil())
        
        expect(config?.portOut).to(beNil())
    }
    
    func testMalformedJsonObjectMapping() {
        let jsonString = """
        {
            "server_addr": invalid_string
        }
        """
        
        let config = TcpPortTaskConfiguration(JSONString: jsonString)
        
        expect(config).to(beNil())
    }
}
