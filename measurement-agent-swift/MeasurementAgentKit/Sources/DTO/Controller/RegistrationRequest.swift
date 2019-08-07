// MeasurementAgentKit: RegistrationRequest.swift, created on 11.04.19
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

/// Registration request DTO. It contains the fields needed to register a new measurement agent.
class RegistrationRequest: Codable {

    // TODO: use identifiers instead of fully qualified class names...
    let deserializeType = "at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest"

    /// Boolean whether the measurement agent has accepted the presented terms and conditions.
    var termsAndConditionsAccepted: Bool?

    /// The version of the presented terms and conditions that the measurement agent agreed to (or declined).
    var termsAndConditionsAcceptedVersion: Int?

    /// The measurement agent's group name/identifier.
    var groupName: String?

    ///
    enum CodingKeys: String, CodingKey {
        case termsAndConditionsAccepted = "terms_and_conditions_accepted"
        case termsAndConditionsAcceptedVersion = "terms_and_conditions_accepted_version"
        case groupName = "group_name"
        case deserializeType = "deserialize_type"
    }
}
