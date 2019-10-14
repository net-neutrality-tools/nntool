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
public class MeasurementServerDto {

	
	/**
	 * The measurement peer's public identifier which is sent back to server by the measurement agent.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The measurement peer's public identifier which is sent back to server by the measurement agent.")
	@JsonPropertyDescription("The measurement peer's public identifier which is sent back to server by the measurement agent.")
	@Expose
	@SerializedName("identifier")
	@JsonProperty(required = true, value = "identifier")
	private String identifier;
	
	/**
	 * The measurement peer's public name.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The measurement peer's public name.")
	@JsonPropertyDescription("The measurement peer's public name.")
	@Expose
	@SerializedName("name")
	@JsonProperty(required = true, value = "name")
	private String name;
	
	/**
	 * Load API URL
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Load API URL")
	@JsonPropertyDescription("Load API URL")
	@Expose
	@SerializedName("load_api_url")
	@JsonProperty(required = true, value = "load_api_url")	
	private String loadApiUrl;

	/**
	 * Load API secret key
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Load API secret key")
	@JsonPropertyDescription("Load API secret key")
	@Expose
	@SerializedName("load_api_secret")
	@JsonProperty(required = true, value = "load_api_secret")
	private String loadApiSecretKey;
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoadApiUrl() {
		return loadApiUrl;
	}

	public void setLoadApiUrl(String loadApiUrl) {
		this.loadApiUrl = loadApiUrl;
	}

	public String getLoadApiSecretKey() {
		return loadApiSecretKey;
	}

	public void setLoadApiSecretKey(String loadApiSecretKey) {
		this.loadApiSecretKey = loadApiSecretKey;
	}

	@Override
	public String toString() {
		return "MeasurementServerDto [identifier=" + identifier + ", name=" + name + ", loadApiUrl=" + loadApiUrl
				+ ", loadApiSecretKey=" + loadApiSecretKey + "]";
	}
}
