import {Traffic} from './traffic.model';
import {NumStreamsInfo} from './num-streams-info.model';
import {WebSocketInfo} from './web-socket-info.model';

export class ConnectionInfo {
    /**
     * The address of the measurement server.
     */
    address: string;

    /**
     * Port used for the communication.
     */
    port: number;

    /**
     * Indicates if the communication with the measurement server will be encrypted.
     */
    encrypted: boolean;

    /**
     * Cryptographic protocol and cipher suite used for encrypted communication, if available.
     * E.g. TLSv1.2 (TLS_RSA_WITH_AES_128_GCM_SHA256).
     */
    encryption_info: string;

    /**
     * Contains information about total bytes transferred during the speed measurement, as reported by the client's interface, if available.
     * Only used for displaying to the client.
     */
    client_interface_total_traffic: Traffic;

    /**
     * Contains information about bytes transferred during the download measurement, as reported by the client's interface, if available.
     * Only used for displaying to the client.
     */
    client_interface_download_measurement_traffic: Traffic;

    /**
     * Contains information about bytes transferred during the upload measurement, as reported by the client's interface, if available.
     * Only used for displaying to the client.
     */
    client_interface_upload_measurement_traffic: Traffic;

    /**
     * @see NumStreamsInfo
     */
    num_streams_info: NumStreamsInfo;

    /**
     * Flag if TCP SACK (Selective Acknowledgement) is enabled/requested.
     * See <a href="https://tools.ietf.org/html/rfc2018">https://tools.ietf.org/html/rfc2018</a>.
     */
    tcp_opt_sack_requested: boolean;

    /**
     * Flag if the TCP window scale options are requested.
     */
    tcp_opt_wscale_requested: boolean;

    /**
     * Maximum Segment Size (MSS) value from the server-side.
     */
    server_mss: number;

    /**
     * Maximum Transmission Unit (MTU) value from the server-side.
     */
    server_mtu: number;

    /**
     * @see WebSocketInfo
     */
    web_socket_info_download: WebSocketInfo;

    /**
     * @see WebSocketInfo
     */
    web_socket_info_upload: WebSocketInfo;
}
