package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;

/**
 * This DTO serves as the base class for specific sub measurement results.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO serves as the base class for specific sub measurement results.")
@JsonClassDescription("This DTO serves as the base class for specific sub measurement results.")
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		property = "deserialize_type"
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = SpeedMeasurementResult.class, name = "speed_result"),
		@JsonSubTypes.Type(value = QoSMeasurementResult.class, name = "qos_result")
})
public abstract class SubMeasurementResult {

// SubMeasurementTime

	/**
	 * Start time in nanoseconds relative to the start time of the overall measurement object.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Start time in nanoseconds relative to the start time of the overall measurement object.")
	@JsonPropertyDescription("Start time in nanoseconds relative to the start time of the overall measurement object.")
	@Expose
	@SerializedName("relative_start_time_ns")
	@JsonProperty(required = true, value = "relative_start_time_ns")
	private Long relativeStartTimeNs;

	/**
	 * End time in nanoseconds relative to the end time of the overall measurement object.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "End time in nanoseconds relative to the end time of the overall measurement object.")
	@JsonPropertyDescription("End time in nanoseconds relative to the end time of the overall measurement object.")
	@Expose
	@SerializedName("relative_end_time_ns")
	@JsonProperty(required = true, value = "relative_end_time_ns")
	private Long relativeEndTimeNs;

// MeasurementStatusInfo

	/**
	 * @see StatusDto
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The status of a measurement.")
	@JsonPropertyDescription("The status of a measurement.")
	@Expose
	@SerializedName("status")
	@JsonProperty(required = true, value = "status")
	private StatusDto status;

	/**
	 * @see ReasonDto
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The reason why a measurement failed.")
	@JsonPropertyDescription("The reason why a measurement failed.")
	@Expose
	@SerializedName("reason")
	@JsonProperty(required = true, value = "reason")
	private ReasonDto reason;

// SubMeasurement

	// We don't need to submit the following values because they are either generated by the server or 
	// already submitted in the initiation request:
	// - token
	// - versionProtocol
	// - versionLibrary

	public Long getRelativeStartTimeNs() {
		return relativeStartTimeNs;
	}

	public void setRelativeStartTimeNs(Long relativeStartTimeNs) {
		this.relativeStartTimeNs = relativeStartTimeNs;
	}

	public Long getRelativeEndTimeNs() {
		return relativeEndTimeNs;
	}

	public void setRelativeEndTimeNs(Long relativeEndTimeNs) {
		this.relativeEndTimeNs = relativeEndTimeNs;
	}

	public StatusDto getStatus() {
		return status;
	}

	public void setStatus(StatusDto status) {
		this.status = status;
	}

	public ReasonDto getReason() {
		return reason;
	}

	public void setReason(ReasonDto reason) {
		this.reason = reason;
	}
}
