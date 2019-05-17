package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains provider related information captured during the test.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains provider related information captured during the test.")
public class ProviderInfo {

	/**
	 * ASN for the public IP address.
	 */
	@JsonPropertyDescription("ASN for the public IP address.")
	@Expose
	@SerializedName("public_ip_asn")
	@JsonProperty("public_ip_asn")
	private Long publicIpAsn;
	
	/**
	 * Name of ASN.
	 */
	@JsonPropertyDescription("Name of ASN.")
	@Expose
	@SerializedName("public_ip_as_name")
	@JsonProperty("public_ip_as_name")
	private String publicIpAsName;
	
	/**
	 * Country code derived from the AS (e.g. "AT").
	 */
	@JsonPropertyDescription("Country code derived from the AS (e.g. \"AT\").")
	@Expose
	@SerializedName("country_code_asn")
	@JsonProperty("country_code_asn")
	private String countryCodeAsn;
	
	/**
	 * @see EmbeddedProvider
	 */
	@JsonPropertyDescription("Contains information about a provider.")
	@Expose
	@SerializedName("provider")
	@JsonProperty("provider")
	private EmbeddedProvider provider;

	public Long getPublicIpAsn() {
		return publicIpAsn;
	}

	public void setPublicIpAsn(Long publicIpAsn) {
		this.publicIpAsn = publicIpAsn;
	}

	public String getPublicIpAsName() {
		return publicIpAsName;
	}

	public void setPublicIpAsName(String publicIpAsName) {
		this.publicIpAsName = publicIpAsName;
	}

	public String getCountryCodeAsn() {
		return countryCodeAsn;
	}

	public void setCountryCodeAsn(String countryCodeAsn) {
		this.countryCodeAsn = countryCodeAsn;
	}

	public EmbeddedProvider getProvider() {
		return provider;
	}

	public void setProvider(EmbeddedProvider provider) {
		this.provider = provider;
	}
}