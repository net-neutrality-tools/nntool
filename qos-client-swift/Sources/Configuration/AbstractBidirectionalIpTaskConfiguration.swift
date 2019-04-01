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

///
public class AbstractBidirectionalIpTaskConfiguration: AbstractControlConnectionTaskConfiguration {

    ///
    enum Direction: String, Codable {
        case unknown = "UNKNOWN"
        case outgoing = "OUT"
        case incoming = "IN"
    }

    ///
    var portOut: UInt16?

    //
    var portIn: UInt16?

    ///
    var direction: Direction {
        if let p = portOut, p > 0 {
            return .outgoing
        }

        if let p = portIn, p > 0 {
            return .incoming
        }

        return .unknown
    }

    public required init() {
        super.init()
    }

    public required init(from decoder: Decoder) throws {
        try super.init(from: decoder)

        let container = try decoder.container(keyedBy: CodingKeysTest2.self)
        portOut = try container.decode(UInt16.self, forKey: .portOut)
        portIn = try container.decode(UInt16.self, forKey: .portIn)
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeysTest.self)
        try container.encode(serverAddress, forKey: .serverAddress)
        try container.encode(serverPort, forKey: .serverPort)
    }

    ///
    enum CodingKeysTest2: String, CodingKey { // had to rename from CodingKeys (see https://bugs.swift.org/browse/SR-6747)
        case portOut = "out_port"
        case portIn = "in_port"
    }
}
