// MeasurementAgentKit: FullSubMeasurementWrapperDto.swift, created on 05.08.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
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

public class FullSubMeasurementWrapperDto: Codable {

    var deserializeType: String?

    public var content: FullSubMeasurement?

    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        deserializeType = try container.decodeIfPresent(String.self, forKey: .deserializeType) ?? ""

        switch deserializeType {
        case "full_qos_measurement":
            content = try FullQoSMeasurement(from: decoder)
        case "full_speed_measurement":
            content = try FullIASMeasurement(from: decoder)
        default:
            break
        }
    }

    ///
    enum CodingKeys: String, CodingKey {
        case deserializeType = "deserialize_type"
    }
}
