// MeasurementAgentKit: FullSubMeasurement.swift, created on 05.08.19
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

/// This DTO class is a base class for all specific full measurement classes.
public class FullSubMeasurement: Codable {

    /// Start Date and time for this (sub-) measurement. Date and time is always stored as UTC.
    public var startTime: Date?

    /// End Date and time for this (sub-) measurement. Date and time is always stored as UTC.
    public var endTime: Date?

    /// Duration of a measurement.
    public var durationNs: UInt64?

    ///
    enum CodingKeys: String, CodingKey {
        case startTime  = "start_time"
        case endTime    = "end_time"
        case durationNs = "duration_ns"
    }
}
