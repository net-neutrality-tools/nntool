package at.alladin.nettest.shared.server.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains cell location information captured during the test.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains cell location information captured during the test.")
public class CellLocationInfo {

	/**
	 * List of captured cell information.
	 */
	@JsonPropertyDescription("List of captured cell information.")
	@Expose
	@SerializedName("cell_locations")
	@JsonProperty("cell_locations")
	private List<CellLocation> cellLocations;

}
