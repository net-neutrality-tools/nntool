/***************************************************************************
* Copyright 2017 appscape gmbh
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
import nntool_shared_swift

class QoSBidirectionalIpTask: QoSControlConnectionTask {

    ///
    enum Direction: String {
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

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys3.self)

        portOut = container.decodeIfPresentWithStringFallback(UInt16.self, forKey: .portOut)
        portIn = container.decodeIfPresentWithStringFallback(UInt16.self, forKey: .portIn)

        try super.init(from: decoder)
    }

    ///
    enum CodingKeys3: String, CodingKey {
        case portOut = "out_port"
        case portIn = "in_port"
    }
}
