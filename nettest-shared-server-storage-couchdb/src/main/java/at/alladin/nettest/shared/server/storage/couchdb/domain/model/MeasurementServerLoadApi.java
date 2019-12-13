package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the Load-API of this measurement server.
 *  
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the Load-API of this measurement server.")
public class MeasurementServerLoadApi {
	
	/**
	 * The URL of the Load-API.
	 */
	@JsonPropertyDescription("The URL of the Load-API.")
	@Expose
	@SerializedName("url")
	@JsonProperty("url")
	private String url;
	
	/**
	 * The secret key.
	 */
	@JsonPropertyDescription("The secret key.")
	@Expose
	@SerializedName("secret_key")
	@JsonProperty("secret_key")
	private String secretKey;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
}
