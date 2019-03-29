package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains all relevant network information of a single point in time.
 * @author lb@alladin.at
 *
 */
@JsonClassDescription("Contains all relevant network information of a single point in time.")
public class NetworkPointInTime {
	
	/**
	 * Time and date the signal information was captured (UTC).
	 */
	@JsonPropertyDescription("Time and date the signal information was captured (UTC).")
	@Expose
	@SerializedName("time")
	@JsonProperty("time")
	private LocalDateTime time;
	
	/**
     * Relative time in nanoseconds (to test begin).
	 */
	@JsonPropertyDescription("Relative time in nanoseconds (to test begin).")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;
	
	/**
	 * Public IP address of the client.
	 */
	@JsonPropertyDescription("Public IP address of the client.")
	@Expose
	@SerializedName("client_public_ip")
	@JsonProperty("client_public_ip")
	private String clientPublicIp;
	
	/**
	 * Country of the client which is gathered by Geo-IP lookup.
	 */
	@JsonPropertyDescription("Country of the client which is gathered by Geo-IP lookup.")
	@Expose
	@SerializedName("client_public_ip_country_code")
	@JsonProperty("client_public_ip_country_code")
	private String clientPublicIpCountryCode;
	
	/**
	 * Reverse DNS for the public IP address.
	 */
	@JsonPropertyDescription("Reverse DNS for the public IP address.")
	@Expose
	@SerializedName("public_ip_rdns")
	@JsonProperty("public_ip_rdns")
	private String publicIpRdns;

	/**
	 * @see EmbeddedNetworkType
	 */
	@JsonPropertyDescription("Contains information about the network type.")
	@Expose
	@SerializedName("network_type")
	@JsonProperty("network_type")
	private EmbeddedNetworkType networkType;
	
	/**
	 * @see ProviderInfo
	 */
	@JsonPropertyDescription("Contains provider related information captured during the test.")
	@Expose
	@SerializedName("provider_info")
	@JsonProperty("provider_info")
	private ProviderInfo providerInfo;
	
	/**
	 * Contains WIFI information, if available.
	 */
	@JsonPropertyDescription("Contains WIFI information, if available.")
	@Expose
	@SerializedName("network_wifi_info")
	@JsonProperty("network_wifi_info")	
	private NetworkWifiInfo networkWifiInfo;
	
	/**
	 * Contains mobile network information, if available.
	 */
	@JsonPropertyDescription("Contains mobile network information, if available.")
	@Expose
	@SerializedName("network_mobile_info")
	@JsonProperty("network_mobile_info")
	private NetworkMobileInfo networkMobileInfo;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Long getRelativeTimeNs() {
		return relativeTimeNs;
	}

	public void setRelativeTimeNs(Long relativeTimeNs) {
		this.relativeTimeNs = relativeTimeNs;
	}

	public String getClientPublicIp() {
		return clientPublicIp;
	}

	public void setClientPublicIp(String clientPublicIp) {
		this.clientPublicIp = clientPublicIp;
	}

	public String getClientPublicIpCountryCode() {
		return clientPublicIpCountryCode;
	}

	public void setClientPublicIpCountryCode(String clientPublicIpCountryCode) {
		this.clientPublicIpCountryCode = clientPublicIpCountryCode;
	}

	public String getPublicIpRdns() {
		return publicIpRdns;
	}

	public void setPublicIpRdns(String publicIpRdns) {
		this.publicIpRdns = publicIpRdns;
	}

	public EmbeddedNetworkType getNetworkType() {
		return networkType;
	}

	public void setNetworkType(EmbeddedNetworkType networkType) {
		this.networkType = networkType;
	}

	public ProviderInfo getProviderInfo() {
		return providerInfo;
	}

	public void setProviderInfo(ProviderInfo providerInfo) {
		this.providerInfo = providerInfo;
	}

	public NetworkWifiInfo getNetworkWifiInfo() {
		return networkWifiInfo;
	}

	public void setNetworkWifiInfo(NetworkWifiInfo networkWifiInfo) {
		this.networkWifiInfo = networkWifiInfo;
	}

	public NetworkMobileInfo getNetworkMobileInfo() {
		return networkMobileInfo;
	}

	public void setNetworkMobileInfo(NetworkMobileInfo networkMobileInfo) {
		this.networkMobileInfo = networkMobileInfo;
	}
}
