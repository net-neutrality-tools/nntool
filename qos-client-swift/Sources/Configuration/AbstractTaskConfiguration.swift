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
public class AbstractTaskConfiguration: Codable {

    var timeoutNs: UInt64 = 10 * NSEC_PER_SEC // TODO: config file

    // deprecated
    var concurrencyGroup: Int?
    var qosTestUid: Int?

    ////

    var timeoutS: Double {
        return Double(timeoutNs) / Double(NSEC_PER_SEC)
    }

    public required init() {

    }

    ///
    enum CodingKeys: String, CodingKey {
        case timeoutNs = "timeout"

        case concurrencyGroup = "concurrency_group"
        case qosTestUid       = "qos_test_uid"
    }
}
