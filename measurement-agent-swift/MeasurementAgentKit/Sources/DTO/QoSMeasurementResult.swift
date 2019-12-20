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
import CodableJSON

/// This DTO contains the QoS measurement results from the measurement agent.
public class QoSMeasurementResult: SubMeasurementResult {

    let deserializeType = "qos_result"

    /// QoS measurement results
    public var objectiveResults: [[String: JSON]]?

    public override init() {
        super.init()
    }

    required init(from decoder: Decoder) throws {
        fatalError("init(from:) has not been implemented")
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeys2.self)

        try container.encode(objectiveResults, forKey: .objectiveResults)

        try container.encode(deserializeType, forKey: .deserializeType)
    }

    ///
    enum CodingKeys2: String, CodingKey {
        case objectiveResults = "results"

        case deserializeType = "deserialize_type"
    }
}
