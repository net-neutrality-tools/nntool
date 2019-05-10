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
public class AbstractTaskResult: Codable {

    public enum Status: String, Codable {
        case unknown = "UNKNOWN"
        case ok = "OK"
        case error = "ERROR"
        case timeout = "TIMEOUT"
    }

    var status: Status?/* = .unknown*/

    var objectiveTimeoutNs: UInt64?

    var objectiveQoSTestUid: Int?

    var taskType: TaskType?

    var startTimeNs: UInt64?
    var durationNs: UInt64?

    required init() {

    }

    ///
    enum CodingKeys: String, CodingKey {
        // status, objectiveTimeoutNs, ... are not mapped here because different QoS
        // tasks have different keys for status...this should be changed

        case objectiveQoSTestUid = "qos_test_uid" // deprecated

        case taskType = "test_type"

        case startTimeNs = "start_time_ns"
        case durationNs = "duration_ns"
    }
}
