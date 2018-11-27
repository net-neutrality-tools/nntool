package at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;

/**
 * Measurement agent registration response object which is returned to the measurement agent after successful registration.
 * For convenience this response also contains the current settings.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Measurement agent registration response object which is returned to the measurement agent after successful registration. For convenience this response also contains the current settings.")
@JsonClassDescription("Measurement agent registration response object which is returned to the measurement agent after successful registration. For convenience this response also contains the current settings.")
public class RegistrationResponse extends BasicResponse {

	/**
	 * The generated measurement agent UUID.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The generated measurement agent UUID.")
	@JsonPropertyDescription("The generated measurement agent UUID.")
	@Expose
	@SerializedName("agent_uuid")
	@JsonProperty(required = true, value = "agent_uuid")
	private String agentUuid;
	
	/**
	 * @see SettingsResponse
	 */
	@io.swagger.annotations.ApiModelProperty("The settings response object sent to the measurement agent.")
	@JsonPropertyDescription("The settings response object sent to the measurement agent.")
	@Expose
	@SerializedName("settings")
	@JsonProperty("settings")
	private SettingsResponse settings;
	
	/**
	 * 
	 * @return
	 */
	public String getAgentUuid() {
		return agentUuid;
	}
	
	/**
	 * 
	 * @param agentUuid
	 */
	public void setAgentUuid(String agentUuid) {
		this.agentUuid = agentUuid;
	}
	
	/**
	 * 
	 * @return
	 */
	public SettingsResponse getSettings() {
		return settings;
	}
	
	/**
	 * 
	 * @param settings
	 */
	public void setSettings(SettingsResponse settings) {
		this.settings = settings;
	}
}
