package at.alladin.nettest.shared.berec.collector.api.v1.dto.client.registration;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.settings.SettingsResponse;

/**
 * Client registration response object which is returned to the client after successful registration.
 * For convenience this response also contains the current settings.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Client registration response object which is returned to the client after successful registration. For convenience this response also contains the current settings.")
@JsonClassDescription("Client registration response object which is returned to the client after successful registration. For convenience this response also contains the current settings.")
public class RegistrationResponse extends BasicResponse {

	/**
	 * The generated client UUID.
	 */
	@io.swagger.annotations.ApiModelProperty("The generated client UUID.")
	@JsonPropertyDescription("The generated client UUID.")
	@Expose
	@SerializedName("client_uuid")
	@JsonProperty("client_uuid")
	private String clientUuid;
	
	/**
	 * @see SettingsResponse
	 */
	@io.swagger.annotations.ApiModelProperty("The settings response object sent to the client.")
	@JsonPropertyDescription("The settings response object sent to the client.")
	@Expose
	@SerializedName("settings")
	@JsonProperty("settings")
	private SettingsResponse settings;
	
	/**
	 * 
	 * @return
	 */
	public String getClientUuid() {
		return clientUuid;
	}
	
	/**
	 * 
	 * @param clientUuid
	 */
	public void setClientUuid(String clientUuid) {
		this.clientUuid = clientUuid;
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
