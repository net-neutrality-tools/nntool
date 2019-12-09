// MeasurementAgentKit: QoSMeasurementType.swift, created on 05.08.19
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

public enum QoSMeasurementType: String, CaseIterable, Codable {
    case tcp = "TCP"
    case udp = "UDP"
    case dns = "DNS"
    case nonTransparentProxy = "NON_TRANSPARENT_PROXY"
    case httpProxy = "HTTP_PROXY"
    case voip = "VOIP"
    case traceroute = "TRACEROUTE"
    case website = "WEBSITE"
    case sip = "SIP"
    case echoProtocol = "ECHO_PROTOCOL"

    case mkitDash = "MKIT_DASH"
    case mkitWebConnectivity = "MKIT_WEB_CONNECTIVITY"
}
