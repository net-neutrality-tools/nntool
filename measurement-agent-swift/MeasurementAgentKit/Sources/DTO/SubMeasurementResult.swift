// MeasurementAgentKit: SubMeasurementResult.swift, created on 15.05.19
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

/// The status of a measurement.
public enum StatusDto: String, Codable {
    case started = "STARTED"
    case finished = "FINISHED"
    case failed = "FAILED"
    case aborted = "ABORTED"
}

/// The reason why a measurement failed.
public enum ReasonDto: String, Codable {

    /// Use this if the connection to the measurement server couldn't be established.
    case unableToConnect = "UNABLE_TO_CONNECT"

    /// Use this if the connection was lost during a measurement.
    case connectionLost = "CONNECTION_LOST"

    /// Use this if the network category changed (e.g. from MOBILE to WIFI).
    case networkCategoryChanged = "NETWORK_CATEGORY_CHANGED"

    /// Use this if the App was put to background on mobile devices.
    case appBackgrounded = "APP_BACKGROUNDED"

    /// Use this if the user aborted the measurement.
    case userAborted = "USER_ABORTED"
}

/// This DTO serves as the base class for specific sub measurement results.
public class SubMeasurementResult: Codable {

    /// Start time in nanoseconds relative to the start time of the overall measurement object.
    public var relativeStartTimeNs: UInt64?

    /// End time in nanoseconds relative to the end time of the overall measurement object.
    public var relativeEndTimeNs: UInt64?

    /// The status of a measurement.
    public var status: StatusDto?

    /// The reason why a measurement failed.
    public var reason: ReasonDto?

    public init() {

    }

    ///
    enum CodingKeys: String, CodingKey {
        case relativeStartTimeNs = "relative_start_time_ns"
        case relativeEndTimeNs = "relative_end_time_ns"
        case status
        case reason
    }
}
