package at.alladin.nettest.shared.server.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about all geographic locations captured during the test.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about all geographic locations captured during the test.")
public class GeoLocationInfo {

	/**
	 * List of all captured geographic locations.
	 */
	@JsonPropertyDescription("List of all captured geographic locations.")
	@Expose
	@SerializedName("geo_locations")
	@JsonProperty("geo_locations")
	private List<GeoLocation> geoLocations;
}
