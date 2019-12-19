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

/// Brief/short information of a QoS measurement.
public class BriefQoSMeasurement: BriefSubMeasurement {

    /// Indicated how many objective where run during the QoS measurement.
    public var objectiveCount: Int?

    public override init() {
        super.init()
    }

    required init(from decoder: Decoder) throws {
        try super.init(from: decoder)

        let container = try decoder.container(keyedBy: CodingKeys2.self)

        try objectiveCount = container.decode(Int.self, forKey: .objectiveCount)
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeys2.self)

        try container.encode(objectiveCount, forKey: .objectiveCount)
    }

    ///
    enum CodingKeys2: String, CodingKey {
         case objectiveCount = "objective_count"
    }
}
