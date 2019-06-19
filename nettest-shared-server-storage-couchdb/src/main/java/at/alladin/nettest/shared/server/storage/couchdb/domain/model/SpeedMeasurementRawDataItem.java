package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about captured byte transfers during the speed measurement from a point of time.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about captured byte transfers during the speed measurement from a point of time.")
public class SpeedMeasurementRawDataItem {
	
	/**
	 * The stream id (numeric value starting from 0).
	 */
	@JsonPropertyDescription("The stream id (numeric value starting from 0).")
	@Expose
	@SerializedName("stream_id")
	@JsonProperty("stream_id")
    private Integer streamId;
    
    /**
     * Relative time since start of the speed measurement.
     */
	@JsonPropertyDescription("Relative time since start of the speed measurement.")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;
    
    /**
     * Bytes transmitted or received since start of the speed measurement.
     */
	@JsonPropertyDescription("Bytes transmitted or received since start of the speed measurement.")
    @Expose
    @SerializedName("bytes")
    @JsonProperty("bytes")
    private Long bytes;

	/**
	 * Bytes transmitted or received since start of the speed measurement, including the slow start phase.
	 */
	@JsonPropertyDescription("Bytes transmitted or received since start of the speed measurement, including the slow start phase.")
	@Expose
	@SerializedName("bytes_including_slow_start")
	@JsonProperty("bytes_including_slow_start")
	private Long bytesIncludingSlowStart;

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

	public Long getBytesIncludingSlowStart() {
		return bytesIncludingSlowStart;
	}

	public void setBytesIncludingSlowStart(Long bytesIncludingSlowStart) {
		this.bytesIncludingSlowStart = bytesIncludingSlowStart;
	}
}