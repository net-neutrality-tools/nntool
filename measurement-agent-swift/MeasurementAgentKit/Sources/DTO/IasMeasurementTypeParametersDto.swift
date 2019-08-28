// MeasurementAgentKit: MeasurementTypeParametersDto.swift, created on 09.05.19
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

public class IasMeasurementTypeParametersDto: MeasurementTypeParametersDto {

    public var measurementConfiguration: IasMeasurementConfiguration?

    public required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        measurementConfiguration = try container.decode(IasMeasurementConfiguration.self, forKey: .measurementConfiguration)

        try super.init(from: decoder)
    }

    ///
    enum CodingKeys: String, CodingKey {
        case measurementConfiguration = "measurement_configuration"
    }
}

public class IasMeasurementConfiguration: Codable {

    public let download: [IasMeasurementClass]
    public let upload: [IasMeasurementClass]

    ///
    enum CodingKeys: String, CodingKey {
        case download
        case upload
    }
}

public class IasMeasurementClass: Codable {

    public class Bounds: Codable {

        public let lowerBound: Double
        public let upperBound: Double

        ///
        enum CodingKeys: String, CodingKey {
            case lowerBound = "lower"
            case upperBound = "upper"
        }
    }

    public let isDefault: Bool? // default is sometimes null in json...
    public let streams: Int
    public let frameSize: Int
    public let framesPerCall: Int?
    public let bounds: Bounds

    ///
    enum CodingKeys: String, CodingKey {
        case isDefault = "default"
        case streams
        case frameSize
        case framesPerCall
        case bounds
    }
}
