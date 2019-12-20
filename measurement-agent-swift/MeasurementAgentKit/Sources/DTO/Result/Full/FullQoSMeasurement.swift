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

/// This DTO class contains all QoS measurement information that is sent to the measurement agent.
public class FullQoSMeasurement: FullSubMeasurement {

    public var results: [EvaluatedQoSResult]?

    public var keyToTranslationMap: [String: String]?

    public var qosTypeToDescriptionMap: [String: QoSTypeDescription]?

    public override init() {
        super.init()
    }

    required init(from decoder: Decoder) throws {
        try super.init(from: decoder)

        let container = try decoder.container(keyedBy: CodingKeys2.self)

        try results = container.decode([EvaluatedQoSResult].self, forKey: .results)
        try keyToTranslationMap = container.decode([String: String].self, forKey: .keyToTranslationMap)
        try qosTypeToDescriptionMap = container.decode([String: QoSTypeDescription].self, forKey: .qosTypeToDescriptionMap)
    }

    public override func encode(to encoder: Encoder) throws {
        try super.encode(to: encoder)

        var container = encoder.container(keyedBy: CodingKeys2.self)

        try container.encode(results, forKey: .results)
        try container.encode(keyToTranslationMap, forKey: .keyToTranslationMap)
        try container.encode(qosTypeToDescriptionMap, forKey: .qosTypeToDescriptionMap)
    }

    ///
    enum CodingKeys2: String, CodingKey {
        case results
        case keyToTranslationMap = "key_to_translation_map"
        case qosTypeToDescriptionMap = "qos_type_to_description_map"
    }
}
