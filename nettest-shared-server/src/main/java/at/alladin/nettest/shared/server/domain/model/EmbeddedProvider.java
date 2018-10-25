package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about a provider.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about a provider.")
public class EmbeddedProvider {

	/**
	 * The name of the provider.
	 */
	@JsonPropertyDescription("The name of the provider.")
	@Expose
	@SerializedName("name")
	@JsonProperty("name")
	private String name;

	/**
	 * The short name (or shortcut) of the provider.
	 */
	@JsonPropertyDescription("The short name (or shortcut) of the provider.")
	@Expose
	@SerializedName("short_name")
	@JsonProperty("short_name")
	private String shortName;

}
