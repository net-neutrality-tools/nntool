/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
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

/// This DTO class contains all measurement information that is sent to the measurement agent.
public class FullMeasurementResponse: Codable {

    /// The unique identifier (UUIDv4) of the measurement.
    public var uuid: String?

    /// The open-data identifier (UUIDv4) of the measurement.
    public var openDataUuid: String?

    /// Measurement system uuid. Can be either own system or imported from open-data.
    public var systemUuid: String?

    /// Contains the result of a Speed and/or QoS measurement.
    public var measurements: [String: FullSubMeasurementWrapperDto]?

    /// Start Date and time for this (sub-) measurement. Date and time is always stored as UTC.
    public var startTime: Date?
    
    //// Measurement date and time in agent's local time.
    public var localTime: Date?

    /// End Date and time for this (sub-) measurement. Date and time is always stored as UTC.
    public var endTime: Date?

    /// Duration of a measurement.
    public var durationNs: UInt64?

    /// List of all captured geographic locations.
    public var geoLocations: [GeoLocationDto]?

    /// Contains information about the measurement measurement agent.
//    public var agentInfo: MeasurementAgentInfoDto?

    /// Contains information about the device the measurement software is running on.
//    public var deviceInfo: DeviceInfoDto?

    /// Contains network related information gathered during the test.
//    public var networkInfo: NetworkInfoDto?

    ///
    enum CodingKeys: String, CodingKey {
        case uuid
        case openDataUuid = "open_data_uuid"
        case systemUuid   = "system_uuid"
        case measurements
        case startTime    = "start_time"
        case endTime      = "end_time"
        case durationNs   = "duration_ns"
        case geoLocations = "geo_locations"
  //      case agentInfo = "agent_info"
  //      case deviceInfo = "device_info"
  //      case networkInfo = "network_info"
    }
}
