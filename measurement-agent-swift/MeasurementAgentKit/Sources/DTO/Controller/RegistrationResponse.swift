// MeasurementAgentKit: RegistrationResponse.swift, created on 29.03.19
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

/// Measurement agent registration response object which is returned to the measurement agent after successful registration.
/// For convenience this response also contains the current settings.
class RegistrationResponse: Codable {

    /// The generated measurement agent UUID.
    var agentUuid: String?

    /// The settings response object sent to the measurement agent.
    var settings: SettingsResponse?

    ///
    enum CodingKeys: String, CodingKey {
        case agentUuid = "agent_uuid"
        case settings
    }
}
