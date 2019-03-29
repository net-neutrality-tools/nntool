package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Brief/short information of a sub measurement.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Brief/short information of a sub measurement.")
@JsonClassDescription("Brief/short information of a sub measurement.")
public class BriefSubMeasurement {

	/**
	 * Start time of this sub measurement in UTC.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Start time of this sub measurement in UTC.")
	@JsonPropertyDescription("Start time of this sub measurement in UTC.")
	@Expose
	@SerializedName("start_time")
	@JsonProperty(required = true, value = "start_time")
	private LocalDateTime startTime;
	
	/**
	 * Duration of this sub measurement.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Duration of this sub measurement.")
	@JsonPropertyDescription("Duration of this sub measurement.")
	@Expose
	@SerializedName("duration_ns")
	@JsonProperty(required = true, value = "duration_ns")
	private Long durationNs;
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	
	public Long getDurationNs() {
		return durationNs;
	}
	
	public void setDurationNs(Long durationNs) {
		this.durationNs = durationNs;
	}
}
