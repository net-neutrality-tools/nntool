/***************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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
 ***************************************************************************/

import Foundation

///
enum TaskType: String, Codable {

    ///
    case tcpPort = "tcp"

    ///
    case udpPort = "udp"

    ///
    case echoProtocol = "echo_protocol"

    ///
    case dns = "dns"

    ///
    case httpProxy = "http_proxy"

    ///
    case nonTransparentProxy = "non_transparent_proxy"

    ///
    case traceroute = "traceroute"

    ///
    case website = "website"

    ///
    case voip = "voip"

    ///
    case sip = "sip"
    
    ///
    case audioStreaming = "audio_streaming"

    ///
    case mkitWebConnectivity = "mkit_web_connectivity"

    ///
    case mkitDash = "mkit_dash"

    ///

    func taskClass() -> QoSTask.Type {
        switch self {
        case .tcpPort:
            return TcpPortTask.self
        case .udpPort:
            return UdpPortTask.self
        case .echoProtocol:
            return EchoProtocolTask.self
        case .dns:
            return DnsTask.self
        case .httpProxy:
            return HttpProxyTask.self
        case .nonTransparentProxy:
            return NonTransparentProxyTask.self
        case .traceroute:
            return TracerouteTask.self
        case .website:
            return WebsiteRenderingTask.self
        case .voip:
            return VoipTask.self
        case .sip:
            return SipTask.self
        case .audioStreaming:
            return AudioStreamingTask.self
        case .mkitWebConnectivity:
            return MeasurementKitTask.self
        case .mkitDash:
            return MeasurementKitTask.self
        }
    }
}
