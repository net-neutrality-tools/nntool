// MeasurementAgentKit: BriefMeasurementResponse.swift, created on 24.07.19
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

/// The BriefMeasurementResponse contains the most important values of a measurement.
/// It is used to show a preview (list) of measurements to the end user.
public class BriefMeasurementResponse: Codable {

    /// The UUIDv4 identifier of the measurement object.
    public var uuid: String?

    /// Overall start time in UTC.
    public var startTime: Date?

    /// Overall duration of all sub measurements.
    public var durationNs: UInt64?

    /// The first accurate GeoLocation i.e. the location where the measurement was started.
    public var firstAccurateGeoLocation: GeoLocationDto?

    /// The type of measurement agent.
    public var agentType: MeasurementAgentTypeDto?

    /// BriefDeviceInfo contains the most important values from DeviceInfo class.
    public var deviceInfo: BriefDeviceInfo?

    /// Network type id.
    public var networkTypeId: Int?

    /// Network type name.
    public var networkTypeName: String?

    /// Map that contains available information for each measurement type (Speed, QoS).
    /// If map misses speed then no speed measurement was done, likewise for QoS, ...
    public var measurements: [String: BriefSubMeasurementWrapperDto]?

    ///
    enum CodingKeys: String, CodingKey {
        case uuid
        case startTime  = "start_time"
        case durationNs = "duration_ns"
        case firstAccurateGeoLocation = "first_accurate_geo_location"
        case agentType = "type"
        case deviceInfo = "device_info"
        case networkTypeId = "network_type_id"
        case networkTypeName = "network_type_name"
        case measurements
    }
}
