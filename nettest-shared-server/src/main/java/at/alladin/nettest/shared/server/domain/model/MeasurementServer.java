package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Measurement server configuration.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Measurement server configuration.")
public class MeasurementServer {
	
	/**
	 * A UUID which serves as primary key.
	 */
	@JsonPropertyDescription("A UUID which serves as primary key.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;
	
	/**
	 * @see MeasurementServerType
	 */
	@JsonPropertyDescription("Measurement server type.")
	@Expose
	@SerializedName("type")
	@JsonProperty("type")
	private MeasurementServerType type;

	/**
	 * Name (label) of this measurement server.
	 */
	@JsonPropertyDescription("Name (label) of this measurement server.")
	@Expose
	@SerializedName("name")
	@JsonProperty("name")
	private String name;
	
	/**
	 * Port used for non-encrypted communication.
	 */
	@JsonPropertyDescription("Port used for non-encrypted communication.")
	@Expose
	@SerializedName("port")
	@JsonProperty("port")
	private Integer port;
	
	/**
	 * Port used for encrypted communication.
	 */
	@JsonPropertyDescription("Port used for encrypted communication.")
	@Expose
	@SerializedName("port_tls")
	@JsonProperty("port_tls")
	private Integer portTls;

	/**
	 * @see MeasurementServerInfo
	 */
	@JsonPropertyDescription("Contains additional detail information about a measurement server.")
	@Expose
	@SerializedName("info")
	@JsonProperty("info")
	private MeasurementServerInfo info;

	/**
	 * The measurement server's IPv4 address or name.
	 */
	@JsonPropertyDescription("The measurement server's IPv4 address or name.")
	@Expose
	@SerializedName("address_ipv4")
	@JsonProperty("address_ipv4")
	private String addressIpv4;

	/**
	 * The measurement server's IPv6 address or name.
	 */
	@JsonPropertyDescription("The measurement server's IPv6 address or name.")
	@Expose
	@SerializedName("address_ipv6")
	@JsonProperty("address_ipv6")
	private String addressIpv6;

	/**
	 * Flag that indicates if this measurement server is enabled.
	 */
	@JsonPropertyDescription("Flag that indicates if this measurement server is enabled.")
	@Expose
	@SerializedName("enabled")
	@JsonProperty("enabled")
	private boolean enabled;

	/**
	 * The measurement server's secret key used to generate measurement tokens.
	 */
	@JsonPropertyDescription("The measurement server's secret key used to generate measurement tokens.")
	@Expose
	@SerializedName("secret_key")
	@JsonProperty("secret_key")
	private String secretKey;
}
