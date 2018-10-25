package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the network type.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the network type.")
public class EmbeddedNetworkType {
	
	/**
	 * Network type name.
	 */
	@JsonPropertyDescription("Network type name.")
	@Expose
	@SerializedName("name")
	@JsonProperty("name")
	private String name;
	
	/**
	 * Network group name (e.g. 2G, 3G, LAN).
	 */
	@JsonPropertyDescription("Network group name (e.g. 2G, 3G, LAN).")
	@Expose
	@SerializedName("group_name")
	@JsonProperty("group_name")
	private String groupName;

	/**
	 * @see NetworkTypeCategory
	 */
	@JsonPropertyDescription("Contains the different network categories.")
	@Expose
	@SerializedName("category")
	@JsonProperty("category")
	private NetworkTypeCategory category;
}
