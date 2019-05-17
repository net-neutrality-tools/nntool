package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the mobile network.
 *  
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the mobile network.")
public class NetworkMobileInfo {

    /**
     * The network operator country code (e.g. "AT"), if available.
     */
	@JsonPropertyDescription("The network operator country code (e.g. \"AT\"), if available.")
    @Expose
	@SerializedName("network_country")
    @JsonProperty("network_country")
    private String networkCountry;
    
    /**
     * The MCC/MNC of the network operator, if available.
     */
	@JsonPropertyDescription("The MCC/MNC of the network operator, if available.")
    @Expose
    @SerializedName("network_operator_mcc_mnc")
    @JsonProperty("network_operator_mcc_mnc")
    private MccMnc networkOperatorMccMnc;
    
    /**
     * The network operator name, if available.
     */
	@JsonPropertyDescription("The network operator name, if available.")
    @Expose
    @SerializedName("network_operator_name")
    @JsonProperty("network_operator_name")
    private String networkOperatorName;
    
    /**
     * The SIM operator country code (e.g. "AT"), if available.
     */
	@JsonPropertyDescription("The SIM operator country code (e.g. \"AT\"), if available.")
    @Expose
    @SerializedName("sim_country")
    @JsonProperty("sim_country")
    private String simCountry;
    
    /**
     * The MCC/MNC of the SIM operator, if available.
     */
	@JsonPropertyDescription("The MCC/MNC of the SIM operator, if available.")
    @Expose
    @SerializedName("sim_operator_mcc_mnc")
    @JsonProperty("sim_operator_mcc_mnc")
    private MccMnc simOperatorMccMnc;
    
    /**
     * SIM operator name, if available.
     */
	@JsonPropertyDescription("SIM operator name, if available.")
    @Expose
    @SerializedName("sim_operator_name")
    @JsonProperty("sim_operator_name")
    private String simOperatorName;
    
    /**
     * Indicates if this is a roaming connection, if available (null = unknown).
     */
	@JsonPropertyDescription("Indicates if this is a roaming connection, if available (null = unknown).")
    @Expose
    @SerializedName("roaming")
    @JsonProperty("roaming")
    private Boolean roaming;

    /**
     * The roaming type, if available.
     */
	@JsonPropertyDescription("The roaming type, if available.")
    @Expose
    @SerializedName("roaming_type")
    @JsonProperty("roaming_type")
    private RoamingType roamingType;

	public String getNetworkCountry() {
		return networkCountry;
	}

	public void setNetworkCountry(String networkCountry) {
		this.networkCountry = networkCountry;
	}

	public MccMnc getNetworkOperatorMccMnc() {
		return networkOperatorMccMnc;
	}

	public void setNetworkOperatorMccMnc(MccMnc networkOperatorMccMnc) {
		this.networkOperatorMccMnc = networkOperatorMccMnc;
	}

	public String getNetworkOperatorName() {
		return networkOperatorName;
	}

	public void setNetworkOperatorName(String networkOperatorName) {
		this.networkOperatorName = networkOperatorName;
	}

	public String getSimCountry() {
		return simCountry;
	}

	public void setSimCountry(String simCountry) {
		this.simCountry = simCountry;
	}

	public MccMnc getSimOperatorMccMnc() {
		return simOperatorMccMnc;
	}

	public void setSimOperatorMccMnc(MccMnc simOperatorMccMnc) {
		this.simOperatorMccMnc = simOperatorMccMnc;
	}

	public String getSimOperatorName() {
		return simOperatorName;
	}

	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}

	public Boolean getRoaming() {
		return roaming;
	}

	public void setRoaming(Boolean roaming) {
		this.roaming = roaming;
	}

	public RoamingType getRoamingType() {
		return roamingType;
	}

	public void setRoamingType(RoamingType roamingType) {
		this.roamingType = roamingType;
	}
}