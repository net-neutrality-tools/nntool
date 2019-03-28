// MeasurementAgentKit: LmapScheduleDto.swift, created on 28.03.19
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

/// Configuration of a particular Schedule.
class LmapScheduleDto: Mappable {

    /// The locally unique, administratively assigned name for this Schedule.
    var name: String?

    /// The event source controlling the start of the scheduled Actions.
    /// Referencing the {@link LmapEventDto#getName()} of an Action.
    var start: String?

    /// This choice contains optional leafs that control the graceful forced termination of scheduled Actions.
    /// When the end has been reached, the scheduled Actions should be forced to terminate the measurements.
    /// This may involve being active some additional time in order to properly finish the Action's activity
    /// (e.g., waiting for any messages that are still outstanding).
    /// If set to a {@link LmapStopDurationDto} it will behave like a typical timeout set for the execution of this schedule.
    var stop: LmapStopDto?

    /// The execution mode of this Schedule determines in which order the Actions of the Schedule are executed.
    /// Supported values are: sequential, pipelined, parallel.
    var executionMode: ExecutionModeDto?

    /// A set of Schedule-specific tags that are reported together with the measurement results to a Collector.
    var tags: [String]?

    /// A set of Suppression tags that are used to select Actions to be suppressed.
    var suppressionTags: [String]?

    /// The current state of the Schedule (One of: enabled, disabled, running, suppressed).
    var state: LmapStateDto?

    /// The amount of secondary storage (e.g., allocated in a file system)
    /// holding temporary data allocated to the Schedule in bytes.
    /// This object reports the amount of allocated physical storage and not the storage used by logical data records.
    var storage: UInt64?

    /// Number of invocations of this Schedule.
    /// This counter does not include suppressed invocations or invocations that were prevented
    /// due to an overlap with a previous invocation of this Schedule.
    var invocations: Int?

    /// Number of suppressed executions of this Schedule.
    var suppressions: Int?

    /// Number of executions prevented due to overlaps with a previous invocation of this Schedule.
    var overlaps: Int?

    /// Number of failed executions of this Schedule. A failed execution is an execution where at least one Action failed.
    var failures: Int?

    /// The date and time of the last invocation of this Schedule.
    var lastInvocation: Date?

    /// An Action describes a Task that is invoked by the Schedule.
    /// Multiple Actions are invoked according to the execution-mode of the Schedule.
    var actions: [LmapActionDto]?

    ///
    public required init?(map: Map) {

    }

    ///
    public func mapping(map: Map) {
        name            <- map["name"]
        start           <- map["start"]
        stop            <- map["stop"]
        executionMode   <- map["execution-mode"]
        tags            <- map["tag"]
        suppressionTags <- map["suppression-tag"]
        state           <- map["state"]
        storage         <- map["storage"]
        invocations     <- map["invocations"]
        suppressions    <- map["suppressions"]
        overlaps        <- map["overlaps"]
        failures        <- map["failures"]
        lastInvocation  <- map["last-invocation"]
        actions         <- map["action"]
    }
}
