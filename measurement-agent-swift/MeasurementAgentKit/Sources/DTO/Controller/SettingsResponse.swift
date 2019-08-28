// MeasurementAgentKit: SettingsResponse.swift, created on 29.03.19
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

/// The settings response object sent to the measurement agent.
class SettingsResponse: Codable {

    /// Object that contains base URLs for controller, collector, map and statistic services.
    var urls: Urls

    /// Map of QoS measurement types to translated type information.
    var qosTypeInfo: [QoSMeasurementType: TranslatedQoSTypeInfo]?

    /// Class for all kind of versions that the server reveals.
    var versions: VersionResponse?

    ///
    enum CodingKeys: String, CodingKey {
        case urls
        case qosTypeInfo = "qos_type_info"
    }

    /// Object that contains base URLs for controller, collector, map and statistic services.
    class Urls: Codable {

        /// Base URL of the controller service of the form "[protocol]://[domain]:[port]/[path]".
        /// This domain name should have A and AAAA records.
        var controllerService: String?

        /// IPv4-only base URL of the controller service.
        /// This domain name must only have an A record.
        var controllerServiceIpv4: String?

        /// IPv6-only base URL of the controller service.
        /// This domain name must only have an AAAA record.
        var controllerServiceIpv6: String?

        /// Base URL of the collector service of the form "[protocol]://[domain]:[port]/[path]".
        /// This domain name should have A and AAAA records.
        var collectorService: String?

        /// Base URL of the map service of the form "[protocol]://[domain]:[port]/[path]".
        var mapService: String?

        /// Base URL of the statistic service of the form "[protocol]://[domain]:[port]/[path]".
        var statisticService: String?

        /// Base URL of the result service of the form "[protocol]://[domain]:[port]/[path]".
        /// This domain name should have A and AAAA records.
        var resultService: String?

        /// Base URL of the web site of the form "[protocol]://[domain]:[port]/[path]".
        var website: String?

        ///
        enum CodingKeys: String, CodingKey {
            case controllerService = "controller_service"
            case controllerServiceIpv4 = "controller_service_ipv4"
            case controllerServiceIpv6 = "controller_service_ipv6"
            case collectorService = "collector_service"
            case mapService = "map_service"
            case statisticService = "statistic_service"
            case resultService = "result_service"
            case website
        }
    }

    /// Contains translated information for each available QoS measurement type.
    class TranslatedQoSTypeInfo: Codable {

        /// The translated QoS type name.
        var name: String?

        /// The translated QoS type description.
        var description: String?

        ///
        enum CodingKeys: String, CodingKey {
            case name
            case description
        }
    }
}
