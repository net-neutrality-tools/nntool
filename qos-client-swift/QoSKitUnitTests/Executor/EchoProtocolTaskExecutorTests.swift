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

///
class EchoProtocolTaskExecutorTests: XCTestCase {

    override func setUp() {
        continueAfterFailure = false
    }
    
    func testTcp() {
        let config = EchoProtocolTaskConfigurationBuilder()
            .timeoutNs(10 * NSEC_PER_SEC)
            .host("localhost")
            .port(7)
            .protocolType(.tcp)
            .payload("PING_tcp")
            .build()
        
        let executor = EchoProtocolTaskExecutor(config: config)
        
        let result = executor.runTask()
        
        print(result.toJSONString(prettyPrint: true)!)
    }
    
    func testUdp() {
        let config = EchoProtocolTaskConfigurationBuilder()
            .timeoutNs(10 * NSEC_PER_SEC)
            .host("localhost")
            .port(7)
            .protocolType(.udp)
            .payload("PING_udp")
            .build()
        
        let executor = EchoProtocolTaskExecutor(config: config)
        
        let result = executor.runTask()
        
        print(result.toJSONString(prettyPrint: true)!)
    }
    
    func testAsync() {
        let expectation = XCTestExpectation(description: "runTask")
        
        let config = EchoProtocolTaskConfiguration()
        config.timeoutNs = 10 * NSEC_PER_SEC
        
        config.host = "localhost"
        config.port = 7
        config.protocolType = .tcp
        config.payload = "HTTP /-get"
        
        let executor = EchoProtocolTaskExecutor(config: config)
        
        executor.runTask(success: { result in
            print(result.toJSONString(prettyPrint: true)!)
            
            expectation.fulfill()
        })
        
        wait(for: [expectation], timeout: 10.0)
    }
}
