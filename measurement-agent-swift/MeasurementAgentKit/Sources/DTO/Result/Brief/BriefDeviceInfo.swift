// MeasurementAgentKit: BriefDeviceInfo.swift, created on 24.07.19
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

/// BriefDeviceInfo contains the most important values from DeviceInfo class.
public class BriefDeviceInfo: Codable {

    /// Device code name.
    public var deviceCodeName: String?

    /// The device name that is commonly known to users (e.g. Google Pixel).
    public var deviceFullName: String?

    /// Device operating system name.
    public var osName: String?

    /// Device operating system version.
    public var osVersion: String?

    /// Average CPU usage during the measurement.
    public var averageCpuUsage: Double?

    /// Average Memory usage during the measurement.
    public var averageMemUsage: Double?

    ///
    enum CodingKeys: String, CodingKey {
        case deviceCodeName  = "device_code_name"
        case deviceFullName = "device_full_name"
        case osName = "os_name"
        case osVersion = "os_version"
        case averageCpuUsage = "avg_cpu_usage"
        case averageMemUsage = "avg_mem_usage"
    }
}
