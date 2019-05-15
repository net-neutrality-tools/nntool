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
public class AbstractControlConnectionTaskConfiguration: AbstractTaskConfiguration {

    ///
    var serverAddress: String?

    ///
    var serverPort: UInt16?

    public required init() {
        super.init()
    }

    public required init(from decoder: Decoder) throws {
        try super.init(from: decoder)

        let container = try decoder.container(keyedBy: CodingKeysTest.self)
        self.serverAddress = try container.decodeIfPresent(String.self, forKey: .serverAddress)
        self.serverPort = try container.decodeIfPresent(UInt16.self, forKey: .serverPort)
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeysTest.self)
        try container.encode(serverAddress, forKey: .serverAddress)
        try container.encode(serverPort, forKey: .serverPort)
    }

    ///
    enum CodingKeysTest: String, CodingKey { // had to rename from CodingKeys (see https://bugs.swift.org/browse/SR-6747)
        case serverAddress = "server_addr"
        case serverPort    = "server_port"
    }
}
