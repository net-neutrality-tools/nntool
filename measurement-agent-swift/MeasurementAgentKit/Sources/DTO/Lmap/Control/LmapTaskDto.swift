// MeasurementAgentKit: LmapTaskDto.swift, created on 28.03.19
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

/// Configuration of LMAP Tasks.
class LmapTaskDto: Mappable {

    /// The unique name of a Task.
    var name: String?

    /// A list of entries in a registry identifying functions.
    var functions: [LmapFunctionDto]?

    /// The (local) program to invoke in order to execute the Task.
    /// If this leaf is not set, then the system will try to identify a suitable program based on the registry information present.
    var program: String?

    /// The list of Task-specific options.
    var options: [LmapOptionDto]?

    /// A set of Task-specific tags that are reported together with the measurement results to a Collector.
    /// A tag can be used, for example, to carry the Measurement Cycle ID.
    var tags: [String]?

    ///
    public required init?(map: Map) {

    }

    ///
    public func mapping(map: Map) {
        name      <- map["name"]
        functions <- map["function"]
        program   <- map["program"]
        options   <- map["option"]
        tags      <- map["tag"]
    }
}
