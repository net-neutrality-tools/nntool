package at.alladin.nettest.shared.server.domain.model;

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

}