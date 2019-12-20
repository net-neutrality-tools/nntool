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

/// Additional information that is sent by measurement agent alongside the request.
/// This contains most information from measurement agentInfo.
class ApiRequestInfo: Codable {

    /// Language specified by the measurement agent.
    var language: String?

    /// The measurement agent's time zone. Is only stored if a measurement is sent to the server.
    var timezone: String?

    /// Type of measurement agent. Can be one of 'MOBILE', 'BROWSER', 'DESKTOP'.
    var agentType: MeasurementAgentTypeDto?

    /// The agent's UUID.
    /// This value is ignored if the resource path already contains the agent's UUID.
    var agentId: String?

    /// Operating system name.
    var osName: String?

    /// Operating system version.
    var osVersion: String?

    /// API level of operating system or SDK (e.g. Android API level or Swift SDK version).
    var apiLevel: String?

    /// Device code name.
    var codeName: String?

    /// Detailed device designation.
    var model: String?

    /// Application version name (e.g. 1.0.0).
    var appVersionName: String?

    /// Application version code number (e.g. 10).
    var appVersionCode: Int?

    /// Git revision name.
    var appGitRevision: String?

    /// The measurement agent device location at the time the request was sent or null if the measurement agent doesn't have location information.
    var geoLocation: GeoLocationDto?

    ///
    enum CodingKeys: String, CodingKey {
        case language
        case timezone
        case agentType = "agent_type"
        case agentId   = "agent_uuid"
        case osName    = "os_name"
        case osVersion = "os_version"
        case apiLevel  = "api_level"
        case codeName  = "code_name"
        case model
        case appVersionName = "app_version_name"
        case appVersionCode = "app_version_code"
        case appGitRevision = "app_git_revision"
        case geoLocation    = "geo_location"
    }
}
