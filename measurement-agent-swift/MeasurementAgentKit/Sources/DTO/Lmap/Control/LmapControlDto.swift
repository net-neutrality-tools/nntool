// MeasurementAgentKit: LmapControlDto.swift, created on 28.03.19
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

/// Configuration and control of a Measurement Agent.
class LmapControlDto: Mappable {

    /// Agent capabilities including a list of supported Tasks.
    var capabilities: LmapCapabilityDto?

    /// Configuration of parameters affecting the whole Measurement Agent.
    var agent: LmapAgentDto?

    /// Configuration of LMAP Tasks.
    var tasks: [LmapTaskDto]?

    /// Configuration of LMAP Schedules. Schedules control which Tasks are executed by the LMAP implementation.
    var schedules: [LmapScheduleDto]?

    /// Suppression information to prevent Schedules or certain Actions from starting.
    var suppressions: [LmapSuppressionDto]?

    /// Configuration of LMAP events.
    /// Implementations may be forced to delay acting upon the occurrence of events in the face of local constraints.
    /// An Action triggered by an event therefore should not rely on the accuracy provided by the scheduler implementation.
    var events: [LmapEventDto]?

    /// Additional information that is sent by agent alongside the request.
    var additionalRequestInfo: ApiRequestInfo?

    ///
    public required init?(map: Map) {

    }

    ///
    public func mapping(map: Map) {
        capabilities <- map["capabilities"]
        agent        <- map["agent"]
        tasks        <- map["tasks"]
        schedules    <- map["schedules"]
        suppressions <- map["suppressions"]
        events       <- map["events"]

        additionalRequestInfo <- map["additional-request-info"]
    }
}
