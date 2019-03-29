package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains additional information about provider's asn/mcc-mnc mappings.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains additional information about provider's asn/mcc-mnc mappings.")
public class Provider extends EmbeddedProvider {
	
	/**
	 * A ID which serves as primary key.
	 */
	@JsonPropertyDescription("A ID which serves as primary key.")
	@Expose
	@SerializedName("id")
	@JsonProperty("id")
	private Long id;

	/**
	 * Contains a list of all valid/possible ASN mappings for this provider.
	 */
	@JsonPropertyDescription("Contains a list of all valid/possible ASN mappings for this provider.")
    @Expose
    @SerializedName("asn_mappings")
    @JsonProperty("asn_mappings")
    private List<ProviderAsnMapping> asnMappings;
    
    /**
     * Contains a list of all valid/possible MCC/MNC mappings for this provider.
     */
	@JsonPropertyDescription("Contains a list of all valid/possible MCC/MNC mappings for this provider.")
    @Expose
    @SerializedName("mcc_mnc_mappings")
    @JsonProperty("mcc_mnc_mappings")
    private List<ProviderMccMncMapping> mccMncMappings;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ProviderAsnMapping> getAsnMappings() {
		return asnMappings;
	}

	public void setAsnMappings(List<ProviderAsnMapping> asnMappings) {
		this.asnMappings = asnMappings;
	}

	public List<ProviderMccMncMapping> getMccMncMappings() {
		return mccMncMappings;
	}

	public void setMccMncMappings(List<ProviderMccMncMapping> mccMncMappings) {
		this.mccMncMappings = mccMncMappings;
	}
}
