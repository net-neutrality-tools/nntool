package at.alladin.nettest.shared.server.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * MCC/MNC mapping configuration used to identify a mobile provider.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("MCC/MNC mapping configuration used to identify a mobile provider.")
public class ProviderMccMncMapping {

	/**
	 * The MCC/MNC of the SIM.
	 */
	@JsonPropertyDescription("The MCC/MNC of the SIM.")
	@Expose
	@SerializedName("sim_mcc_mnc")
	@JsonProperty("sim_mcc_mnc")
	private MccMnc simMccMnc;
	
	/**
	 * The MCC/MNC of the network.
	 */
	@JsonPropertyDescription("The MCC/MNC of the network.")
	@Expose
	@SerializedName("network_mcc_mnc")
	@JsonProperty("network_mcc_mnc")
	private MccMnc networkMccMnc;
	
	/**
	 * Optional condition used to set the date this MCC/MNC mapping is valid from.
	 */
	@JsonPropertyDescription("Optional condition used to set the date this MCC/MNC mapping is valid from.")
	@Expose
	@SerializedName("condition_valid_from")
	@JsonProperty("condition_valid_from")
	private LocalDateTime conditionValidFrom;
	
	/**
	 * Optional condition used to set the date this MCC/MNC mapping is valid to.
	 */
	@JsonPropertyDescription("Optional condition used to set the date this MCC/MNC mapping is valid to.")
	@Expose
	@SerializedName("condition_valid_to")
	@JsonProperty("condition_valid_to")
	private LocalDateTime conditionValidTo;
	
}
