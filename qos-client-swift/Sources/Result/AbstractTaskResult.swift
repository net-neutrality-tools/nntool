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
public class AbstractTaskResult: Mappable {

    public enum Status: String {
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

    public required init() {

    }

    public required init?(map: Map) {

    }

    public func mapping(map: Map) {
        // status, objectiveTimeoutNs, ... are not mapped here because different QoS
        // tasks have different keys for status...this should be changed

        objectiveQoSTestUid <- map["qos_test_uid"] // deprecated

        taskType <- (map["test_type"], EnumTransform<TaskType>())

        startTimeNs <- map["start_time_ns"]
        durationNs <- map["duration_ns"]
    }
}
