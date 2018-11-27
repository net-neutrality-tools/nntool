package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Measurement detail group object which contains a translated title, an optional description, and icon and the items.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Measurement detail group object which contains a translated title, an optional description, and icon and the items.")
@JsonClassDescription("Measurement detail group object which contains a translated title, an optional description, and icon and the items.")
public class DetailMeasurementGroup {
	
	/**
	 * The already translated title of the given group.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The already translated title of the given group.")
	@JsonPropertyDescription("The already translated title of the given group.")
	@Expose
	@SerializedName("title")
	@JsonProperty(required = true, value = "title")
	private String title;
	
	/**
	 * The already translated (optional) description of the given group.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The already translated (optional) description of the given group.")
	@JsonPropertyDescription("The already translated (optional) description of the given group.")
	@Expose
	@SerializedName("description")
	@JsonProperty(required = true, value = "description")
	private String description;
	
	/**
	 * The icon to be used for the given group (as a single char in the corresponding icon font).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The icon to be used for the given group (as a single char in the corresponding icon font).")
	@JsonPropertyDescription("The icon to be used for the given group (as a single char in the corresponding icon font).")
	@Expose
	@SerializedName("icon_character")
	@JsonProperty(required = true, value = "icon_character")
	private String iconCharacter;
	
	/**
	 * Contains all the entries of the given group.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Contains all the entries of the given group.")
	@JsonPropertyDescription("Contains all the entries of the given group.")
	@Expose
	@SerializedName("items")
	@JsonProperty(required = true, value = "items")
	private List<DetailMeasurementGroupItem> items;
}
