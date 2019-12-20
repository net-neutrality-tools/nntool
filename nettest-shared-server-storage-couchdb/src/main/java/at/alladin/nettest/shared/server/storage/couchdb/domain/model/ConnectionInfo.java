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

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

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
	 * The ip address of the measurement server (can be either v4 or v6).
	 */
	@JsonPropertyDescription("The ip address of the measurement server (can be either v4 or v6).")
	@Expose
	@SerializedName("ip_address")
	@JsonProperty("ip_address")
	private String ipAddress;

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
	 * Contains information about bytes transferred and received by the speed measurement.
	 * 
	 */
	@JsonPropertyDescription("Contains information about traffic volume of the speed measurement based on TCP payload.")
	@Expose
	@SerializedName("tcp_payload_total_bytes")
	@JsonProperty("tcp_payload_total_bytes")
	private Long tcpPayloadTotalBytes;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}

	public String getEncryptionInfo() {
		return encryptionInfo;
	}

	public void setEncryptionInfo(String encryptionInfo) {
		this.encryptionInfo = encryptionInfo;
	}

	public Traffic getClientInterfaceTotalTraffic() {
		return clientInterfaceTotalTraffic;
	}

	public void setClientInterfaceTotalTraffic(Traffic clientInterfaceTotalTraffic) {
		this.clientInterfaceTotalTraffic = clientInterfaceTotalTraffic;
	}

	public Traffic getClientInterfaceDownloadMeasurementTraffic() {
		return clientInterfaceDownloadMeasurementTraffic;
	}

	public void setClientInterfaceDownloadMeasurementTraffic(Traffic clientInterfaceDownloadMeasurementTraffic) {
		this.clientInterfaceDownloadMeasurementTraffic = clientInterfaceDownloadMeasurementTraffic;
	}

	public Traffic getClientInterfaceUploadMeasurementTraffic() {
		return clientInterfaceUploadMeasurementTraffic;
	}

	public void setClientInterfaceUploadMeasurementTraffic(Traffic clientInterfaceUploadMeasurementTraffic) {
		this.clientInterfaceUploadMeasurementTraffic = clientInterfaceUploadMeasurementTraffic;
	}

	public NumStreamsInfo getNumStreamsInfo() {
		return numStreamsInfo;
	}

	public void setNumStreamsInfo(NumStreamsInfo numStreamsInfo) {
		this.numStreamsInfo = numStreamsInfo;
	}

	public Boolean getTcpOptSackRequested() {
		return tcpOptSackRequested;
	}

	public void setTcpOptSackRequested(Boolean tcpOptSackRequested) {
		this.tcpOptSackRequested = tcpOptSackRequested;
	}

	public Boolean getTcpOptWscaleRequested() {
		return tcpOptWscaleRequested;
	}

	public void setTcpOptWscaleRequested(Boolean tcpOptWscaleRequested) {
		this.tcpOptWscaleRequested = tcpOptWscaleRequested;
	}

	public Integer getServerMss() {
		return serverMss;
	}

	public void setServerMss(Integer serverMss) {
		this.serverMss = serverMss;
	}

	public Integer getServerMtu() {
		return serverMtu;
	}

	public void setServerMtu(Integer serverMtu) {
		this.serverMtu = serverMtu;
	}

	public WebSocketInfo getWebSocketInfoDownload() {
		return webSocketInfoDownload;
	}

	public void setWebSocketInfoDownload(WebSocketInfo webSocketInfoDownload) {
		this.webSocketInfoDownload = webSocketInfoDownload;
	}

	public WebSocketInfo getWebSocketInfoUpload() {
		return webSocketInfoUpload;
	}

	public void setWebSocketInfoUpload(WebSocketInfo webSocketInfoUpload) {
		this.webSocketInfoUpload = webSocketInfoUpload;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Long getTcpPayloadTotalBytes() {
		return tcpPayloadTotalBytes;
	}

	public void setTcpPayloadTotalBytes(Long tcpPayloadTotalBytes) {
		this.tcpPayloadTotalBytes = tcpPayloadTotalBytes;
	}	
}
