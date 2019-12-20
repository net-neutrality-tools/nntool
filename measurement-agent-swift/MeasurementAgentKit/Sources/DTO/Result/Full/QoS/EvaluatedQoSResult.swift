/*******************************************************************************
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
 ******************************************************************************/

import Foundation

/// This DTO class contains a single evaluated QoS objective.
public class EvaluatedQoSResult: Codable {

    /// Enum that holds the names of the available QoS measurement objectives.
    public var type: QoSMeasurementType?

    /// The already translated summary for this QoS measurement.
    public var summary: String?

    /// The already translated (and evaluated) description for this QoS measurement.
    public var description: String?

    /// The number of QoS objective evaluations that were run.
    public var evaluationCount: Int?

    /// The number of successful QoS objective evaluations.
    public var successCount: Int?

    /// The number of failed QoS objective evaluations (= evaluationCount - successCount).
    public var failureCount: Int?

    /// Flag to mark this QoS measurement as implausible.
    /// An implausible QoS result is one that could not have been measured that way.
    /// This flag exists for potentially tampered results, so they do not count as valid results (e.g. to prevent tampering w/statistics).
    public var implausible: Bool = false

    /// Contains all evaluated result keys mapped to their respective results.
    public var evaluationKeyMap: [String: String]?

    /// Contains all to be displayed result keys mapped to the corresponding QoSResultOutcome.
    public var resultKeyMap: [String: QoSResultOutcome]?

    enum CodingKeys: String, CodingKey {
        case type
        case summary
        case description
        case evaluationCount  = "evaluation_count"
        case successCount     = "success_count"
        case failureCount     = "failure_count"
        case implausible
        case evaluationKeyMap = "evaluation_keys"
        case resultKeyMap     = "result_keys"
    }
}
