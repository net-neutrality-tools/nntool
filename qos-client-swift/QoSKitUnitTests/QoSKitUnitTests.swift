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

///
class QoSKitUnitTests: XCTestCase {

    func testRunner() {

        let objectives = """
        {
          "TCP" : [ {
            "concurrency_group" : "200",
            "qos_test_uid" : "200",
            "out_port" : "5542",
            "qostest" : "tcp"
          }, {
            "concurrency_group" : "200",
            "qos_test_uid" : "201",
            "out_port" : "5545",
            "qostest" : "tcp"
          } ],
          "UDP" : [ {
            "concurrency_group" : "201",
            "out_num_packets" : "1",
            "qos_test_uid" : "250",
            "out_port" : "5004",
            "qostest" : "udp"
          }, {
            "concurrency_group" : "201",
            "out_num_packets" : "1",
            "qos_test_uid" : "251",
            "out_port" : "5005",
            "qostest" : "udp"
          } ],
          "ECHO_PROTOCOL" : [ {
            "concurrency_group" : "301",
            "qos_test_uid" : "301",
            "qostest" : "echo_protocol",
            "echo_protocol_objective_protocol" : "udp",
            "echo_protocol_objective_payload" : "UPD payload",
            "server_addr" : "10.9.8.39",
            "server_port" : "7"
          }, {
            "concurrency_group" : "302",
            "qos_test_uid" : "302",
            "qostest" : "echo_protocol",
            "echo_protocol_objective_protocol" : "tcp",
            "echo_protocol_objective_payload" : "TCP payload",
            "server_addr" : "10.9.8.39",
            "server_port" : "7"
          } ]
        }
        """

        /*let objectives = """
        {
          "TCP" : [ {
            "concurrency_group" : 200,
            "qos_test_uid" : "200",
            "out_port" : "5542",
            "qostest" : "tcp"
          }, {
            "concurrency_group" : 200,
            "qos_test_uid" : "201",
            "out_port" : "5545",
            "qostest" : "tcp"
          } ],
          "UDP" : [ {
            "concurrency_group" : 201,
            "out_num_packets" : "1",
            "qos_test_uid" : "250",
            "out_port" : "5004",
            "qostest" : "udp"
          }, {
            "concurrency_group" : 201,
            "out_num_packets" : "1",
            "qos_test_uid" : "251",
            "out_port" : "5005",
            "qostest" : "udp"
          } ],
          "ECHO_PROTOCOL" : [ {
            "concurrency_group" : 301,
            "qos_test_uid" : "301",
            "qostest" : "echo_protocol",
            "echo_protocol_objective_protocol" : "udp",
            "echo_protocol_objective_payload" : "UPD payload",
            "server_addr" : "10.9.8.39",
            "server_port" : "7"
          }, {
            "concurrency_group" : 302,
            "qos_test_uid" : "302",
            "qostest" : "echo_protocol",
            "echo_protocol_objective_protocol" : "tcp",
            "echo_protocol_objective_payload" : "TCP payload",
            "server_addr" : "10.9.8.39",
            "server_port" : "7"
          } ]
        }
        """*/

        let data = objectives.data(using: .utf8)!

        let d = JSONDecoder()

        d.keyDecodingStrategy = .custom { keys in
            print(keys)
            self.pr(keys)

            let lastComponent = keys.last!.stringValue.split(separator: ".").last!
            print(lastComponent)
            self.pr(lastComponent)

            return AnyKey(stringValue: String(lastComponent))!
        }

        do {
            let res = try d.decode([String: [[String: String]]].self, from: data)
            print(res)
        } catch {
            print(error)
        }
    }

    func pr(_ t: Any) {
        print(t)
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
