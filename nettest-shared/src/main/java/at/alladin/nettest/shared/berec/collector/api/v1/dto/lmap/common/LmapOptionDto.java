package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementTypeParameters;

/**
 * Options may be used to identify the role of a Task or to pass a Channel name to a Task.
 * @author fk
 *
 */
@io.swagger.annotations.ApiModel(description = "Options may be used to identify the role of a Task or to pass a Channel name to a Task.")
@JsonClassDescription("Options may be used to identify the role of a Task or to pass a Channel name to a Task.")
@JsonInclude(Include.NON_EMPTY)
public class LmapOptionDto {
	
	/**
	 * An identifier uniquely identifying an option.
	 * This identifier is required by YANG to uniquely identify a name/value pair,
	 * but it otherwise has no semantic value.
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "An identifier uniquely identifying an option. This identifier is required by YANG to uniquely identify a name/value pair, but it otherwise has no semantic value.")
	@JsonPropertyDescription("An identifier uniquely identifying an option. This identifier is required by YANG to uniquely identify a name/value pair, but it otherwise has no semantic value.")
	@Expose
	@SerializedName("id")
	@JsonProperty(required = false, value = "id")
	private String id;
	
	/**
	 * The name of the option.
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "The name of the option.")
	@JsonPropertyDescription("The name of the option.")
	@Expose
	@SerializedName("name")
	@JsonProperty(required = false, value = "name")
	private String name;
	
	/**
	 * The value of the option.
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "The value of the option.")
	@JsonPropertyDescription("The value of the option.")
	@Expose
	@SerializedName("value")
	@JsonProperty(required = false, value = "value")
	private String value;
	
	/**
	 * The additional measurement parameters of the option.
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "The additional measurement parameters of the option.")
	@JsonPropertyDescription("The additional measurement parameters of the option.")
	@Expose
	@SerializedName("measurement-parameters")
	@JsonProperty(required = false, value = "measurement-parameters")
	private MeasurementTypeParameters measurementParameters;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MeasurementTypeParameters getMeasurementParameters() {
		return measurementParameters;
	}

	public void setMeasurementParameters(MeasurementTypeParameters measurementParameters) {
		this.measurementParameters = measurementParameters;
	}
}
