package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

/**
 * Measurement server configuration.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Measurement server configuration.")
@Document("MeasurementServer")
public class MeasurementServer {
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	private String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public MeasurementServer() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
	/**
	 * A UUID which serves as primary key.
	 */
	@JsonPropertyDescription("A UUID which serves as primary key.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;
	
	/**
	 * The public identifier of this measurement peer (e.g. used in speed measurement peer list).
	 */
	@JsonPropertyDescription("The public identifier of this measurement peer.")
	@Expose
	@SerializedName("public_identifier")
	@JsonProperty("public_identifier")
	private String publicIdentifier;
	
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
	
	@JsonPropertyDescription("Description of this measurement server.")
	@Expose
	@SerializedName("description")
	@JsonProperty("description")
	private String description;
	
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
	 * If TLS encrypted should be preferred.
	 */
	@JsonPropertyDescription("If TLS encrypted should be preferred.")
	@Expose
	@SerializedName("prefer_encryption")
	@JsonProperty("prefer_encryption")
	private boolean preferEncryption;

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
	
	@JsonPropertyDescription("Flag that indicates if this measurement server is the default.")
	@Expose
	@SerializedName("default")
	@JsonProperty("default")
	private boolean defaultPeer;

	/**
	 * The measurement server's secret key used to generate measurement tokens.
	 */
	@JsonPropertyDescription("The measurement server's secret key used to generate measurement tokens.")
	@Expose
	@SerializedName("secret_key")
	@JsonProperty("secret_key")
	private String secretKey;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPublicIdentifier() {
		return publicIdentifier;
	}
	
	public void setPublicIdentifier(String publicIdentifier) {
		this.publicIdentifier = publicIdentifier;
	}
	
	public MeasurementServerType getType() {
		return type;
	}

	public void setType(MeasurementServerType type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getPortTls() {
		return portTls;
	}

	public void setPortTls(Integer portTls) {
		this.portTls = portTls;
	}
	
	public boolean isPreferEncryption() {
		return preferEncryption;
	}
	
	public void setPreferEncryption(boolean preferEncryption) {
		this.preferEncryption = preferEncryption;
	}

	public MeasurementServerInfo getInfo() {
		return info;
	}

	public void setInfo(MeasurementServerInfo info) {
		this.info = info;
	}

	public String getAddressIpv4() {
		return addressIpv4;
	}

	public void setAddressIpv4(String addressIpv4) {
		this.addressIpv4 = addressIpv4;
	}

	public String getAddressIpv6() {
		return addressIpv6;
	}

	public void setAddressIpv6(String addressIpv6) {
		this.addressIpv6 = addressIpv6;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isDefaultPeer() {
		return defaultPeer;
	}
	
	public void setDefaultPeer(boolean defaultPeer) {
		this.defaultPeer = defaultPeer;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
