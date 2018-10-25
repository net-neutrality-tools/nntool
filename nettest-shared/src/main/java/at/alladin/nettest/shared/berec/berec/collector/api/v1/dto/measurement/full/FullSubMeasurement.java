package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;

/**
 * This DTO class is a base class for all specific full measurement classes.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO class is a base class for all specific full measurement classes.")
@JsonClassDescription("This DTO class is a base class for all specific full measurement classes.")
public class FullSubMeasurement {

	/**
	 * Flag to mark a measurement as implausible.
	 */
	@io.swagger.annotations.ApiModelProperty("Flag to mark a measurement as implausible.")
	@JsonPropertyDescription("Flag to mark a measurement as implausible.")
	@Expose
	@SerializedName("implausible")
	@JsonProperty("implausible")
	private boolean implausible;
	
	/**
	 * The protocol version this measurement used, if available.
	 */
	@io.swagger.annotations.ApiModelProperty("The protocol version this measurement used, if available.")
	@JsonPropertyDescription("The protocol version this measurement used, if available.")
	@Expose
	@SerializedName("version_protocol")
	@JsonProperty("version_protocol")
	private String versionProtocol;
	
	/**
	 * The library version this measurement used, if available.
	 */
	@io.swagger.annotations.ApiModelProperty("The library version this measurement used, if available.")
	@JsonPropertyDescription("The library version this measurement used, if available.")
	@Expose
	@SerializedName("version_library")
	@JsonProperty("version_library")
	private String versionLibrary;
	
// SubMeasurementTime
	
	/**
	 * Start time in nanoseconds relative to the start time of the overall measurement object.
	 */
	@io.swagger.annotations.ApiModelProperty("Start time in nanoseconds relative to the start time of the overall measurement object.")
	@JsonPropertyDescription("Start time in nanoseconds relative to the start time of the overall measurement object.")
	@Expose
	@SerializedName("relative_start_time_ns")
	@JsonProperty("relative_start_time_ns")
	private Long relativeStartTimeNs;
	
	/**
	 * End time in nanoseconds relative to the end time of the overall measurement object.
	 */
	@io.swagger.annotations.ApiModelProperty("End time in nanoseconds relative to the end time of the overall measurement object.")
	@JsonPropertyDescription("End time in nanoseconds relative to the end time of the overall measurement object.")
	@Expose
	@SerializedName("relative_end_time_ns")
	@JsonProperty("relative_end_time_ns")
	private Long relativeEndTimeNs;
	
	/**
	 * Start Date and time for this sub measurement. Date and time is always stored as UTC. 
	 */
	@io.swagger.annotations.ApiModelProperty("Start Date and time for this sub measurement. Date and time is always stored as UTC.")
	@JsonPropertyDescription("Start Date and time for this sub measurement. Date and time is always stored as UTC.")
	@Expose
	@SerializedName("start_time")
	@JsonProperty("start_time")
	private LocalDateTime startTime;
	
	/**
	 * End Date and time for this sub measurement. Date and time is always stored as UTC.
	 */
	@io.swagger.annotations.ApiModelProperty("End Date and time for this sub measurement. Date and time is always stored as UTC.")
	@JsonPropertyDescription("End Date and time for this sub measurement. Date and time is always stored as UTC.")
	@Expose
	@SerializedName("end_time")
	@JsonProperty("end_time")
	private LocalDateTime endTime;
	
	/**
	 * Duration of a measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("Duration of a measurement.")
	@JsonPropertyDescription("Duration of a measurement.")
	@Expose
	@SerializedName("duration_ns")
	@JsonProperty("duration_ns")
	private Long durationNs;
	
// MeasurementStatusInfo
	
	/**
	 * @see StatusDto
	 */
	@io.swagger.annotations.ApiModelProperty("The status of a measurement.")
	@JsonPropertyDescription("The status of a measurement.")
	@Expose
	@SerializedName("status")
	@JsonProperty("status")
	private StatusDto status;
	
	/**
	 * @see ReasonDto
	 */
	@io.swagger.annotations.ApiModelProperty("The reason why a measurement failed.")
	@JsonPropertyDescription("The reason why a measurement failed.")
	@Expose
	@SerializedName("reason")
	@JsonProperty("reason")
	private ReasonDto reason;

	public boolean isImplausible() {
		return implausible;
	}

	public void setImplausible(boolean implausible) {
		this.implausible = implausible;
	}

	public String getVersionProtocol() {
		return versionProtocol;
	}

	public void setVersionProtocol(String versionProtocol) {
		this.versionProtocol = versionProtocol;
	}

	public String getVersionLibrary() {
		return versionLibrary;
	}

	public void setVersionLibrary(String versionLibrary) {
		this.versionLibrary = versionLibrary;
	}

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

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Long getDurationNs() {
		return durationNs;
	}

	public void setDurationNs(Long durationNs) {
		this.durationNs = durationNs;
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
