package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains network address translation related information.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains network address translation related information.")
public class NatTypeInfo {

	/**
	 * Type of network address translation.
	 */
	@JsonPropertyDescription("Type of network address translation.")
	@Expose
	@SerializedName("nat_type")
	@JsonProperty("nat_type")
	private NatType natType;

	/**
	 * Network address translation IP version.
	 */
	@JsonPropertyDescription("Network address translation IP version.")
	@Expose
	@SerializedName("ip_version")
	@JsonProperty("ip_version")
	private Integer ipVersion;
	
	/**
	 * Tells if any type of network address translation has been detected.
	 */
	@JsonPropertyDescription("Tells if any type of network address translation has been detected.")
	@Expose
	@SerializedName("is_behind_nat")
	@JsonProperty("is_behind_nat")
	private Boolean isBehindNat;

	public NatType getNatType() {
		return natType;
	}

	public void setNatType(NatType natType) {
		this.natType = natType;
	}

	public Boolean getIsBehindNat() {
		return isBehindNat;
	}

	public void setIsBehindNat(Boolean isBehindNat) {
		this.isBehindNat = isBehindNat;
	}
	
	public Integer getIpVersion() {
		return ipVersion;
	}

	public void setIpVersion(Integer ipVersion) {
		this.ipVersion = ipVersion;
	}

	@Override
	public String toString() {
		return "NatTypeInfo [natType=" + natType + ", ipVersion=" + ipVersion + ", isBehindNat=" + isBehindNat + "]";
	}

}
