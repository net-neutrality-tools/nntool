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

import { NumStreamsInfo } from './num-streams-info.model';
import { Traffic } from './traffic.model';
import { WebSocketInfo } from './web-socket-info.model';

export class ConnectionInfo {
  /**
   * The address of the measurement server.
   */
  public address: string;

  /**
   * The identifier of the measurement server.
   */
  public identifier: string;

  /**
   * Port used for the communication.
   */
  public port: number;

  /**
   * Indicates if the communication with the measurement server will be encrypted.
   */
  public encrypted: boolean;

  /**
   * Cryptographic protocol and cipher suite used for encrypted communication, if available.
   * E.g. TLSv1.2 (TLS_RSA_WITH_AES_128_GCM_SHA256).
   */
  public encryption_info: string;

  /**
   * Contains information about total bytes transferred during the speed measurement, as reported by the client's interface, if available.
   * Only used for displaying to the client.
   */
  public client_interface_total_traffic: Traffic;

  /**
   * Contains information about bytes transferred during the download measurement, as reported by the client's interface, if available.
   * Only used for displaying to the client.
   */
  public client_interface_download_measurement_traffic: Traffic;

  /**
   * Contains information about bytes transferred during the upload measurement, as reported by the client's interface, if available.
   * Only used for displaying to the client.
   */
  public client_interface_upload_measurement_traffic: Traffic;

  /**
   * @see NumStreamsInfo
   */
  public num_streams_info: NumStreamsInfo;

  /**
   * Flag if TCP SACK (Selective Acknowledgement) is enabled/requested.
   * See <a href="https://tools.ietf.org/html/rfc2018">https://tools.ietf.org/html/rfc2018</a>.
   */
  public tcp_opt_sack_requested: boolean;

  /**
   * Flag if the TCP window scale options are requested.
   */
  public tcp_opt_wscale_requested: boolean;

  /**
   * Maximum Segment Size (MSS) value from the server-side.
   */
  public server_mss: number;

  /**
   * Maximum Transmission Unit (MTU) value from the server-side.
   */
  public server_mtu: number;

  /**
   * @see WebSocketInfo
   */
  public web_socket_info_download: WebSocketInfo;

  /**
   * @see WebSocketInfo
   */
  public web_socket_info_upload: WebSocketInfo;
}
