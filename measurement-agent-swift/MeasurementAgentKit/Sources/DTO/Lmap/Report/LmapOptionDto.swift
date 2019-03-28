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
import ObjectMapper

/// Options may be used to identify the role of a Task or to pass a Channel name to a Task.
class LmapOptionDto: Mappable {

    /// An identifier uniquely identifying an option.
    /// This identifier is required by YANG to uniquely identify a name/value pair,
    /// but it otherwise has no semantic value.
    var id: String?

    /// The name of the option.
    var name: String?

    /// The value of the option.
    var value: String?

    /// The additional measurement parameters of the option.
    var measurementParameters: MeasurementTypeParametersDto? // TODO: should be moved to LmapResultDto

    ///
    public required init?(map: Map) {

    }

    ///
    public func mapping(map: Map) {
        id    <- map["id"]
        name  <- map["name"]
        value <- map["value"]

        measurementParameters <- map["measurement-parameters"]
    }
}
