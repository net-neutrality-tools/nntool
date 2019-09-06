package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;

/**
 * Contains additional information about provider's asn/mcc-mnc mappings.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains additional information about provider's asn/mcc-mnc mappings.")
public class Provider extends EmbeddedProvider {
	
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
	
	public Provider() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRev() {
		return rev;
	}
	
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public String getDocType() {
		return docType;
	}
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
