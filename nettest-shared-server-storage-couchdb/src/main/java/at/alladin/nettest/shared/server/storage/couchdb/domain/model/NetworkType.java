package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Extends the EmbeddedNetworkType with an additional uuid for database access of that NetworkType.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Extends the EmbeddedNetworkType with an additional uuid for database access of that NetworkType.")
public class NetworkType extends EmbeddedNetworkType {

	/**
	 * Network type id as it gets returned by the Android API (serves as primary key).
	 */
	@JsonPropertyDescription("Network type id as it gets returned by the Android API (serves as primary key).")
	@Expose
	@SerializedName("id")
	@JsonProperty("id")
	private Long id;
	
}
