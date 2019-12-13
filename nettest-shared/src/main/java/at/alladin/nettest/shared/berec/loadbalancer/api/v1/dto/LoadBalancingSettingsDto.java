package at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb@alladin.at
 *
 */

public class LoadBalancingSettingsDto {

	
	/**
	 * The UUID of the default measurement peer.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The UUID of the default measurement peer.")
	@JsonPropertyDescription("The UUID of the default measurement peer.")
	@Expose
	@SerializedName("default_measurement_peer_uuid")
	@JsonProperty(required = true, value = "default_measurement_peer_uuid")
	private String defaultMeasurementServerUuid;
	
	/**
	 * The URL of the load balancing service to obtain next free measurement server.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The URL of the load balancing service to obtain next free measurement server.")
	@JsonPropertyDescription("The URL of the load balancing service to obtain next free measurement server.")
	@Expose
	@SerializedName("next_free_url")
	@JsonProperty(required = true, value = "next_free_url")
	private String nextFreeUrl;
	
	/**
	 * The URL of the load balancing service to obtain the status/load of a specific measurement server.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The URL of the load balancing service to obtain the status/load of a specific measurement server.")
	@JsonPropertyDescription("The URL of the load balancing service to obtain the status/load of a specific measurement server")
	@Expose
	@SerializedName("server_load_url")
	@JsonProperty(required = true, value = "server_load_url")
	private String serverLoadUrl;

	public String getDefaultMeasurementServerUuid() {
		return defaultMeasurementServerUuid;
	}

	public void setDefaultMeasurementServerUuid(String defaultMeasurementServerUuid) {
		this.defaultMeasurementServerUuid = defaultMeasurementServerUuid;
	}

	public String getNextFreeUrl() {
		return nextFreeUrl;
	}

	public void setNextFreeUrl(String nextFreeUrl) {
		this.nextFreeUrl = nextFreeUrl;
	}

	public String getServerLoadUrl() {
		return serverLoadUrl;
	}

	public void setServerLoadUrl(String serverLoadUrl) {
		this.serverLoadUrl = serverLoadUrl;
	}
}
