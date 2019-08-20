// MeasurementAgentKit: QoSTypeDescription.swift, created on 05.08.19
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

/// This DTO class contains all QoS measurement information that is sent to the measurement agent.
public class QoSTypeDescription: Codable {

    /// The translated name of the QoS type.
    public var name: String?

    /// The translated description of the QoS type.
    public var description: String?

    /// The icon of the QoS type.
    public var icon: String?

    enum CodingKeys: String, CodingKey {
        case name
        case description
        case icon
    }
}
