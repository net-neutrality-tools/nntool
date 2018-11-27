package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Holds a value from a point in time.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T> Type of the value object
 */
@io.swagger.annotations.ApiModel(description = "Holds a measurement agent-side value from a point in time.")
@JsonClassDescription("Holds a measurement agent-side value from a point in time.")
public class PointInTimeValueDto<T> {

	/**
	 * The relative time in nanoseconds to the test start.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The relative time in nanoseconds to the measurement start.")
	@JsonPropertyDescription("The relative time in nanoseconds to the measurement start.")
	@Expose
	@SerializedName("relative_time_ns")
	@JsonProperty(required = true, value = "relative_time_ns")
	private Long relativeTimeNs;
	
	/**
	 * The value recorded at this point in time.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The value recorded at this point in time.")
	@JsonPropertyDescription("The value recorded at this point in time.")
	@Expose
	@SerializedName("value")
	@JsonProperty(required = true, value = "value")
	private T value;

	public Long getRelativeTimeNs() {
		return relativeTimeNs;
	}

	public void setRelativeTimeNs(Long relativeTimeNs) {
		this.relativeTimeNs = relativeTimeNs;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
