// MeasurementAgentKit: TimeBasedResultDto.swift, created on 28.03.19
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

/// This module defines a data model for reporting time based results from Measurement Agents.
class TimeBasedResultDto: Codable {

    /// Start date and time for this measurement. Date and time is always stored as UTC.
    var startTime: Date?

    /// End date and time for this measurement. Date and time is always stored as UTC.
    var endTime: Date?

    /// Overall duration of this measurement.
    var durationNs: UInt64?

    /// List of all captured geographic locations.
    var geoLocations: [GeoLocationDto]?

    /// CPU usage during the test, if available.
    var cpuUsage: [PointInTimeValueDto<Double>]?

    /// Memory usage during the test, if available.
    var memUsage: [PointInTimeValueDto<Double>]?

    /// Contains all relevant network information of a single point in time.
    var networkPointsInTime: [MeasurementResultNetworkPointInTimeDto]?

    /// List of captured cell information.
    var cellLocations: [CellLocationDto]?

    /// List of captured signal information.
    var signals: [SignalDto]?

    ///
    enum CodingKeys: String, CodingKey {
        case startTime           = "start_time"
        case endTime             = "end_time"
        case durationNs          = "duration_ns"
        case geoLocations        = "geo_locations"
        case cpuUsage            = "cpu_usage"
        case memUsage            = "mem_usage"
        case networkPointsInTime = "network_points_in_time"
        case cellLocations       = "cell_locations"
        case signals
    }
}
