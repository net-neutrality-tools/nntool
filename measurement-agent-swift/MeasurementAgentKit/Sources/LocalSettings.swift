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

///
class LocalSettings: Codable {

    var uuid: String?

    var tcAcceptedVersion: Int?

    var configuredControllerServiceBaseUrl: String

    var controllerServiceBaseUrl: String
    var controllerServiceBaseUrlIpv4: String?
    var controllerServiceBaseUrlIpv6: String?

    var resultServiceBaseUrl: String?

    var mapServiceBaseUrl: String?

    var websiteBaseUrl: String?

    init(controllerServiceBaseUrl: String) {
        self.configuredControllerServiceBaseUrl = controllerServiceBaseUrl
        self.controllerServiceBaseUrl = controllerServiceBaseUrl
    }

    ///
    enum CodingKeys: String, CodingKey {
        case uuid
        case tcAcceptedVersion = "tc_accepted_version"
        case configuredControllerServiceBaseUrl = "configured_controller_service_base_url"
        case controllerServiceBaseUrl = "controller_service_base_url"
        case controllerServiceBaseUrlIpv4 = "controller_service_base_url_ipv4"
        case controllerServiceBaseUrlIpv6 = "controller_service_base_url_ipv6"
        case resultServiceBaseUrl = "result_service_base_url"
        case mapServiceBaseUrl = "map_service_base_url"
        case websiteBaseUrl = "website_base_url"
    }
}

extension LocalSettings: CustomDebugStringConvertible {

    var debugDescription: String {
        return "uuid: \(uuid)"
    }
}
