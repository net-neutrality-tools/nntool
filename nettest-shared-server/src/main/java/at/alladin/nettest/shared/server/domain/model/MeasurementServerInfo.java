package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains additional detail information about a measurement server.
 *  
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains additional detail information about a measurement server.")
public class MeasurementServerInfo {
	
	/**
	 * The city the measurement server is located in.
	 */
	@JsonPropertyDescription("The city the measurement server is located in.")
	@Expose
	@SerializedName("city")
	@JsonProperty("city")
	private String city;
	
	/**
	 * The country the measurement server is located in.
	 */
	@JsonPropertyDescription("The country the measurement server is located in.")
	@Expose
	@SerializedName("country")
	@JsonProperty("country")
	private String country;

	/**
	 * The geographic location of the measurement server.
	 */
	@JsonPropertyDescription("The geographic location of the measurement server.")
	@Expose
	@SerializedName("geo_location")
	@JsonProperty("geo_location")
	private GeoLocation geoLocation;

}
