package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the connection(s) used for the speed measurement.
 *
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the connection(s) used for the speed measurement.")
public class ConnectionInfo {

	/**
	 * The address of the measurement server.
	 */
	@JsonPropertyDescription("The address of the measurement server.")
	@Expose
	@SerializedName("address")
	@JsonProperty("address")
	private String address;

	/**
	 * Port used for the communication.
	 */
	@JsonPropertyDescription("Port used for the communication.")
	@Expose
	@SerializedName("port")
	@JsonProperty("port")
	private Integer port;

	/**
	 * Indicates if the communication with the measurement server will be encrypted.
	 */
	@JsonPropertyDescription("Indicates if the communication with the measurement server will be encrypted.")
	@Expose
	@SerializedName("encrpyted")
	@JsonProperty("encrypted")
	private boolean encrypted;

	/**
	 * Cryptographic protocol and cipher suite used for encrypted communication, if available. E.g. TLSv1.2 (TLS_RSA_WITH_AES_128_GCM_SHA256).
	 */
	@JsonPropertyDescription("Cryptographic protocol and cipher suite used for encrypted communication, if available. E.g. TLSv1.2 (TLS_RSA_WITH_AES_128_GCM_SHA256).")
	@Expose
	@SerializedName("encryption_info")
	@JsonProperty("encryption_info")
	private String encryptionInfo;

	/**
	 * Contains information about total bytes transferred during the speed measurement, as reported by the client's interface, if available.
	 * Only used for displaying to the client.
	 */
	@JsonPropertyDescription("Contains information about total bytes transferred during the speed measurement, as reported by the client's interface, if available. Only used for displaying to the client.")
	@Expose
	@SerializedName("client_interface_total_traffic")
	@JsonProperty("client_interface_total_traffic")
	private Traffic clientInterfaceTotalTraffic;

	/**
	 * Contains information about bytes transferred during the download measurement, as reported by the client's interface, if available.
	 * Only used for displaying to the client.
	 */
	@JsonPropertyDescription("Contains information about bytes transferred during the download measurement, as reported by the client's interface, if available. Only used for displaying to the client.")
	@Expose
	@SerializedName("client_interface_download_measurement_traffic")
	@JsonProperty("client_interface_download_measurement_traffic")
	private Traffic clientInterfaceDownloadMeasurementTraffic;

	/**
	 * Contains information about bytes transferred during the upload measurement, as reported by the client's interface, if available.
	 * Only used for displaying to the client.
	 */
	@JsonPropertyDescription("Contains information about bytes transferred during the upload measurement, as reported by the client's interface, if available. Only used for displaying to the client.")
	@Expose
	@SerializedName("client_interface_upload_measurement_traffic")
	@JsonProperty("client_interface_upload_measurement_traffic")
	private Traffic clientInterfaceUploadMeasurementTraffic;

	/**
	 * @see NumStreamsInfo
	 */
	@JsonPropertyDescription("Contains information about requested number of streams and the actual number of streams used during the speed measurement.")
	@Expose
	@SerializedName("num_streams_info")
	@JsonProperty("num_streams_info")
	private NumStreamsInfo numStreamsInfo;

	/**
	 * Flag if TCP SACK (Selective Acknowledgement) is enabled/requested.
	 * See <a href="https://tools.ietf.org/html/rfc2018">https://tools.ietf.org/html/rfc2018</a>.
	 */
	@JsonPropertyDescription("Flag if TCP SACK (Selective Acknowledgement) is enabled/requested.")
	@Expose
	@SerializedName("tcp_opt_sack_requested")
	@JsonProperty("tcp_opt_sack_requested")
	private Boolean tcpOptSackRequested;

	/**
	 * Flag if the TCP window scale options are requested.
	 */
	@JsonPropertyDescription("Flag if the TCP window scale options are requested.")
	@Expose
	@SerializedName("tcp_opt_wscale_requested")
	@JsonProperty("tcp_opt_wscale_requested")
	private Boolean tcpOptWscaleRequested;

	/**
	 * Maximum Segment Size (MSS) value from the server-side.
	 */
	@JsonPropertyDescription("Maximum Segment Size (MSS) value from the server-side.")
	@Expose
	@SerializedName("server_mss")
	@JsonProperty("server_mss")
	private Integer serverMss;

	/**
	 * Maximum Transmission Unit (MTU) value from the server-side.
	 */
	@JsonPropertyDescription("Maximum Transmission Unit (MTU) value from the server-side.")
	@Expose
	@SerializedName("server_mtu")
	@JsonProperty("server_mtu")
	private Integer serverMtu;

	/**
	 * @see WebSocketInfo
	 */
	@JsonPropertyDescription("This class contains additional information gathered from the WebSocket protocol during the download measurement.")
	@Expose
	@SerializedName("web_socket_info_download")
	@JsonProperty("web_socket_info_download")
	private WebSocketInfo webSocketInfoDownload;

	/**
	 * @see WebSocketInfo
	 */
	@JsonPropertyDescription("This class contains additional information gathered from the WebSocket protocol during the upload measurement.")
	@Expose
	@SerializedName("web_socket_info_upload")
	@JsonProperty("web_socket_info_upload")
	private WebSocketInfo webSocketInfoUpload;
}
