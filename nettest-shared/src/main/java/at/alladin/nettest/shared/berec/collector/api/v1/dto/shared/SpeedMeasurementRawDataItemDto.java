package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about captured byte transfers during the speed measurement from a point of time on the measurement agent.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Contains information about captured byte transfers during the speed measurement from a point of time on the measurement agent.")
@JsonClassDescription("Contains information about captured byte transfers during the speed measurement from a point of time on the measurement agent.")
public class SpeedMeasurementRawDataItemDto {

	/**
	 * The stream id (numeric value starting from 0).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The stream id (numeric value starting from 0).")
	@JsonPropertyDescription("The stream id (numeric value starting from 0).")
	@Expose
	@SerializedName("stream_id")
	@JsonProperty(required = true, value = "stream_id")
    private Integer streamId;
    
    /**
     * Relative time since start of the speed measurement.
     */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Relative time since start of the speed measurement.")
	@JsonPropertyDescription("Relative time since start of the speed measurement.")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty(required = true, value = "relative_time_ns")
    private Long relativeTimeNs;
    
    /**
     * Bytes transmitted or received since start of the speed measurement.
     */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Bytes transmitted or received since start of the speed measurement.")
	@JsonPropertyDescription("Bytes transmitted or received since start of the speed measurement.")
    @Expose
    @SerializedName("bytes")
    @JsonProperty(required = true, value = "bytes")
    private Long bytes;

	public Integer getStreamId() {
		return streamId;
	}

	public void setStreamId(Integer streamId) {
		this.streamId = streamId;
	}

	public Long getRelativeTimeNs() {
		return relativeTimeNs;
	}

	public void setRelativeTimeNs(Long relativeTimeNs) {
		this.relativeTimeNs = relativeTimeNs;
	}

	public Long getBytes() {
		return bytes;
	}

	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}
}
