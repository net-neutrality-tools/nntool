// MeasurementAgentKit: LmapOptionDto.swift, created on 28.03.19
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

/// Options may be used to identify the role of a Task or to pass a Channel name to a Task.
public class LmapOptionDto: Codable {

    /// An identifier uniquely identifying an option.
    /// This identifier is required by YANG to uniquely identify a name/value pair,
    /// but it otherwise has no semantic value.
    var id: String?

    /// The name of the option.
    public var name: String?

    /// The value of the option.
    public var value: String?

    /// The additional measurement parameters of the option.
    public var measurementParameters: MeasurementTypeParametersWrapperDto?/*MeasurementTypeParametersDto?*/ // TODO: should be moved to LmapResultDto

    ///
    enum CodingKeys: String, CodingKey {
        case id
        case name
        case value
        case measurementParameters = "measurement-parameters"
    }
}
