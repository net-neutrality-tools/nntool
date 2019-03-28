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
import ObjectMapper

///
public class EchoProtocolTaskConfiguration: AbstractTaskConfiguration {

    static let ProtocolTypeTransformOf = TransformOf<ProtocolType, String>(
        fromJSON: { ProtocolType(rawValue: $0 ?? "") },
        toJSON: { $0?.rawValue }
    )

    public enum ProtocolType: String {
        case tcp
        case udp
    }

    var host: String?
    var port: UInt16? = 7
    var protocolType: ProtocolType? = .udp
    var payload: String?

    // packetCount?

    public override func mapping(map: Map) {
        super.mapping(map: map)

        host <- map["host"]
        port <- map["port"]
        protocolType <- (map["protocol"], EchoProtocolTaskConfiguration.ProtocolTypeTransformOf)
        payload <- map["payload"]
    }
}
