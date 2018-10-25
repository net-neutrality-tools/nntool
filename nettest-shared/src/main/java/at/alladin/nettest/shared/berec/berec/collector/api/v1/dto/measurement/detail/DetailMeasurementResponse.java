package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;

/**
 * This DTO contains a list of detail groups.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO contains a list of detail groups.")
@JsonClassDescription("This DTO contains a list of detail groups.")
public class DetailMeasurementResponse extends BasicResponse {

	/**
	 * A list of detail groups.
	 */
	@io.swagger.annotations.ApiModelProperty("A list of detail groups.")
	@JsonPropertyDescription("A list of detail groups.")
	@Expose
	@SerializedName("groups")
	@JsonProperty("groups")
	private List<DetailMeasurementGroup> groups;
	
}
