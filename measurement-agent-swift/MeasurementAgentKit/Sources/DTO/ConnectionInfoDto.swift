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

/// Holds information about the received and transmitted amount of data on the measurement agent.
public class TrafficDto: Codable {

    /// Bytes received.
    public var bytesRx: UInt64?

    /// Bytes transmitted.
    public var bytesTx: UInt64?

    ///
    enum CodingKeys: String, CodingKey {
        case bytesRx = "bytes_rx"
        case bytesTx = "bytes_tx"
    }

    public class func fromInterfaceTraffic(_ it: InterfaceTraffic) -> TrafficDto {
        let t = TrafficDto()

        t.bytesTx = UInt64(it.tx)
        t.bytesRx = UInt64(it.rx)

        return t
    }
}

/// Contains information about the connection(s) used for the speed measurement.
public class ConnectionInfoDto: Codable {

    /// The address of the measurement server.
    public var address: String?

    /// Port used for the communication.
    public var port: UInt16?

    /// Indicates if the communication with the measurement server will be encrypted.
    public var encrypted: Bool?

    /// Cryptographic protocol and cipher suite used for encrypted communication, if available. E.g. TLSv1.2 (TLS_RSA_WITH_AES_128_GCM_SHA256).
    public var encryptionInfo: String?

    /// Contains information about total bytes transferred during the speed measurement, as reported by the measurement agent's interface, if available.
    public var agentInterfaceTotalTraffic: TrafficDto?

    /// Contains information about bytes transferred during the download measurement, as reported by the measurement agent's interface, if available.
    public var agentInterfaceDownloadMeasurementTraffic: TrafficDto?

    /// Contains information about bytes transferred during the upload measurement, as reported by the measurement agent's interface, if available.
    public var agentInterfaceUploadMeasurementTraffic: TrafficDto?

    /// The requested number of streams for the download measurement.
    public var requestedNumStreamsDownload: Int?

    /// The requested number of streams for the upload measurement.
    public var requestedNumStreamsUpload: Int?

    /// The actual number of streams used by the download measurement.
    public var actualNumStreamsDownload: Int?

    /// The actual number of streams used by the upload measurement.
    public var actualNumStreamsUpload: Int?

    /// Flag if TCP SACK (Selective Acknowledgement) is enabled/requested.
    public var tcpOptSackRequested: Bool?

    /// Flag if the TCP window scale options are requested.
    public var tcpOptWscaleRequested: Bool?

    /// Maximum Segment Size (MSS) value from the server-side.
    public var serverMss: Int?

    /// Maximum Transmission Unit (MTU) value from the server-side.
    public var serverMtu: Int?

    /// @see WebSocketInfo
    public var webSocketInfoDownload: WebSocketInfoDto?

    /// @see WebSocketInfo
    public var webSocketInfoUpload: WebSocketInfoDto?

    public init() {

    }

    ///
    enum CodingKeys: String, CodingKey {
        case address
        case port
        case encrypted
        case encryptionInfo = "encryption_info"
        case agentInterfaceTotalTraffic = "agent_interface_total_traffic"
        case agentInterfaceDownloadMeasurementTraffic = "agent_interface_download_measurement_traffic"
        case agentInterfaceUploadMeasurementTraffic = "agent_interface_upload_measurement_traffic"
        case requestedNumStreamsDownload = "requested_num_streams_download"
        case requestedNumStreamsUpload = "requested_num_streams_upload"
        case actualNumStreamsDownload = "actual_num_streams_download"
        case actualNumStreamsUpload = "actual_num_streams_upload"
        case tcpOptSackRequested = "tcp_opt_sack_requested"
        case tcpOptWscaleRequested = "tcp_opt_wscale_requested"
        case serverMss = "server_mss"
        case serverMtu = "server_mtu"
        case webSocketInfoDownload = "web_socket_info_download"
        case webSocketInfoUpload = "web_socket_info_upload"
    }
}
