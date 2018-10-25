package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * ASN mapping configuration used to identify a fixed-line provider.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("ASN mapping configuration used to identify a fixed-line provider.")
public class ProviderAsnMapping {

	/**
	 * The Autonomous System Number (ASN).
	 */
	@JsonPropertyDescription("The Autonomous System Number (ASN).")
	@Expose
	@SerializedName("asn")
	@JsonProperty("asn")
	private long asn;
	
	/**
	 * The reverse DNS suffix, an optional condition for an ASN mapping (e.g.: .com).
	 */
	@JsonPropertyDescription("The reverse DNS suffix, an optional condition for an ASN mapping (e.g.: .com).")
	@Expose
	@SerializedName("condition_rdns_suffix")
	@JsonProperty("condition_rdns_suffix")
	private String conditionRdnsSuffix;
}
