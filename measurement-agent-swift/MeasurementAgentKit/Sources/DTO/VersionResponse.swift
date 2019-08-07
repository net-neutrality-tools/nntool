// MeasurementAgentKit: VersionResponse.swift, created on 29.03.19
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

/// Class for all kind of versions that the server reveals.
public class VersionResponse: Codable {

    /// Controller service version number.
    public var controllerServiceVersion: String?

    /// Collector service version number.
    public var collectorServiceVersion: String?

    /// Result service version number.
    public var resultServiceVersion: String?

    /// Map service version number.
    public var mapServiceVersion: String?

    /// Statistic service version number.
    public var statisticServiceVersion: String?

    ///
    enum CodingKeys: String, CodingKey {
        case controllerServiceVersion = "controller_service_version"
        case collectorServiceVersion  = "collector_service_version"
        case resultServiceVersion     = "result_service_version"
        case mapServiceVersion        = "map_service_version"
        case statisticServiceVersion  = "statistic_service_version"
    }
}
