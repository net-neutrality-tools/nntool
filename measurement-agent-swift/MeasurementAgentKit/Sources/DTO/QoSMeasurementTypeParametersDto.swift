// MeasurementAgentKit: MeasurementTypeParametersDto.swift, created on 06.05.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
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
 ******************************************************************************/

import Foundation

/*class ObjectiveTest: Codable {

    var uid: String // TODO: refactor to UInt
    var concurrencyGroup: UInt
    var type: String
    var timeout: UInt64?

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        uid = try container.decode(String.self, forKey: .uid)
        concurrencyGroup = try container.decode(UInt.self, forKey: .concurrencyGroup)
        type = try container.decode(String.self, forKey: .type)
        
        if let timeoutStr = try container.decodeIfPresent(String.self, forKey: .timeout) {
            if let t = UInt64(timeoutStr) {
                timeout = t
            } else {
                throw NSError(domain: "err", code: 0, userInfo: nil)
            }
        }
        
        /*let c2 = try decoder.container(keyedBy: Coding2.self)
        try c2.decode(<#T##type: Bool.Type##Bool.Type#>, forKey: .tcpTest)*/
    }

    func encode(to encoder: Encoder) throws {
        // TODO
    }
    
    enum Coding2: String, CodingKey {
        
        case tcpTest = "tcp_test"
        
        
    }

    ///
    enum CodingKeys: String, CodingKey {
        case uid = "qos_test_uid"
        case concurrencyGroup = "concurrency_group"
        case type = "qostest"
        case timeout
    }
}*/

public class QoSMeasurementTypeParametersDto: MeasurementTypeParametersDto {

    public var objectives: [String: [[String: String]]]?
    //public var objectives: [String: [[String: ObjectiveValue]]]?
    //public var objectives: [String: Any]?

    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        //var test = try container.decodeAny(forKey: .objectivesStr)
        //var nested = try container.nestedUnkeyedContainer(forKey: .objectives)
        //var nested = try container.nestedContainer(keyedBy: CodingKeys.self, forKey: .objectives)
        //var test = try nested.decode([String: Any].self) //as? [String: [[String: Any]]]
        //var test = try container.decode([String: Any].self, forKey: .objectives) //as? [String: [[String: Any]]]

        let objectivesTest = try container.decode([String: [[String: ObjectiveValue]]].self, forKey: .objectives)
        //var test = try container.decode([String: [ObjectiveTest]].self, forKey: .objectives)
        //print(test)

        objectives = [String: [[String: String]]]()

        for item in objectivesTest {
            let (k, v) = item

            var newArray = [[String: String]]()

            for var (index, i) in v.enumerated() {
                newArray.append(
                    i.filter({ !$1.isNil })
                    .mapValues({ (objval) -> String in
                        switch objval {
                        case .int(let o): return "\(o)"
                        case .double(let o): return "\(o)"
                        case .bool(let o): return "\(o)"
                        case .string(let o): return o
                        default:
                            // never happens
                            return "..."
                        }
                    }))
            }

            objectives?[k] = newArray
        }

        print("OBJECTIVES")
        print(objectives)
        print("/OBJECTIVES")

        //var objectives: [String: [Dictionary<String, AnyObject>]] = try container.decodeIfPresent(/*[String: [[String: Any]]]*/[String: [Dictionary<String, Any>]].self, forKey: .objectivesStr)
        /*let objectivesStr = try container.decode(String.self, forKey: .objectivesStr)
        if let objectivesData = objectivesStr.data(using: .utf8) {
            objectives = try JSONSerialization.jsonObject(with: objectivesData, options: []) as? [String: [[String: Any]]]
        }*/

        try super.init(from: decoder)
    }

    ///
    enum CodingKeys: String, CodingKey {
        case objectives
    }
}
