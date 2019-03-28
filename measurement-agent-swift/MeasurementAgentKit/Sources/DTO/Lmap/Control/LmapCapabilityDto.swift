// MeasurementAgentKit: LmapCapabilityDto.swift, created on 28.03.19
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

/// Agent capabilities including a list of supported Tasks.
class LmapCapabilityDto: Mappable {

    /// A short description of the software implementing the Measurement Agent.
    /// This should include the version number of the Measurement Agent software.
    var version: String?

    /// An optional unordered set of tags that provide additional information about the capabilities of the Measurement Agent.
    var tags: [String]?

    /// A list of Tasks that the Measurement Agent supports.
    var tasks: [LmapCapabilityTaskDto]?

    ///
    public required init?(map: Map) {

    }

    ///
    public func mapping(map: Map) {
        version <- map["version"]
        tags    <- map["tag"]
        tasks   <- map["tasks"]
    }
}
