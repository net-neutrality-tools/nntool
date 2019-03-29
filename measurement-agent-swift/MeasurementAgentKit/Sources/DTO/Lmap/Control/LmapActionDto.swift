// MeasurementAgentKit: LmapActionDto.swift, created on 28.03.19
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

/// An Action describes a Task that is invoked by the Schedule.
/// Multiple Actions are invoked according to the execution-mode of the Schedule.
class LmapActionDto: Codable {

    /// The unique identifier for this Action.
    var name: String?

    /// The Task invoked by this Action.
    var taskName: String?

    /// This container is a placeholder for runtime parameters
    /// defined in Task-specific data models augmenting the base LMAP report data model.
    var parameters: String? //Any?

    /// The list of Action-specific options that are appended to the list of Task-specific options.
    var options: [LmapOptionDto]?

    /// A set of Schedules receiving the output produced by this Action.
    /// The output is stored temporarily since the Destination Schedules will in general not be running when output is passed to them.
    /// The behavior of an Action passing data to its own Schedule is implementation specific.
    var destinations: [String]?

    /// A set of Action-specific tags that are reported together with the measurement results to a Collector.
    var tags: [String]?

    /// A set of Suppression tags that are used to select Actions to be suppressed.
    var suppressionTags: [String]?

    /// The current state of the Action (One of: enabled, disabled, running, suppressed).
    var state: LmapStateDto?

    /// The amount of secondary storage (e.g., allocated in a file system)
    /// holding temporary data allocated to the Schedule in bytes.
    /// This object reports the amount of allocated physical storage and not the storage used by logical data records.
    var storage: UInt64?

    /// Number of invocations of this Action.
    /// This counter does not include suppressed invocations or invocations that were prevented
    /// due to an overlap with a previous invocation of this Action.
    var invocations: Int?

    /// Number of suppressed executions of this Action.
    var suppressions: Int?

    /// Number of executions prevented due to overlaps with a previous invocation of this Action.
    var overlaps: Int?

    /// Number of failed executions of this Action.
    var failures: Int?

    /// The date and time of the last invocation of this Action.
    var lastInvocation: Date?

    /// The date and time of the last completion of this Action.
    var lastCompletion: Date?

    /// The status code returned by the last execution of this Action (with 0 indicating successful execution).
    var lastStatus: Int?

    /// The status message produced by the last execution of this Action.
    var lastMessage: String?

    /// The date and time of the last failed completion of this Action.
    var lastFailedCompletion: Date?

    /// The status code returned by the last failed execution of this Action.
    var lastFailedStatus: Int?

    /// The status message produced by the last failed execution of this Action.
    var lastFailedMessage: String?

    ///
    enum CodingKeys: String, CodingKey {
        case name
        case parameters
        case options         = "option"
        case destinations    = "destination"
        case tags            = "tag"
        case suppressionTags = "suppression-tag"
        case state
        case storage
        case invocations
        case suppressions
        case overlaps
        case failures

        case lastInvocation       = "last-invocation"
        case lastCompletion       = "last-completion"
        case lastStatus           = "last-status"
        case lastMessage          = "last-message"
        case lastFailedCompletion = "last-failed-completion"
        case lastFailedStatus     = "last-failed-status"
        case lastFailedMessage    = "last-failed-message"
    }
}
