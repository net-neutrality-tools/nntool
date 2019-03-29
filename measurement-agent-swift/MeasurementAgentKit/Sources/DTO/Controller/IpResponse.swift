// MeasurementAgentKit: IpResponse.swift, created on 29.03.19
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

/// Response object sent to the measurement agent after a successful IP request.
class IpResponse: Codable {

    /// The measurement agent's public IP address.
    var ipAddress: String?

    /// The measurement agent's public IP version (IPv4 or IPv6).
    var ipVersion: IpVersion?

    ///
    enum CodingKeys: String, CodingKey {
        case ipAddress = "ip_address"
        case ipVersion = "ip_version"
    }
}

/// IP version (IPv4 or IPv6).
enum IpVersion: String, Codable {

    ///
    case ipv4 = "IPv4"

    ///
    case ipv6 = "IPv6"
}
