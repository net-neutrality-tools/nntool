package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information from a single round trip time measurement.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information from a single round trip time measurement.")
public class Rtt {

	/**
	 * Round trip time recorded in nanoseconds.
	 */
	@JsonPropertyDescription("Round trip time recorded in nanoseconds.")
	@Expose
	@SerializedName("rtt_ns")
	@JsonProperty("rtt_ns")
	private Long rttNs;

	/**
     * Relative time in nanoseconds (to test begin).
	 */
	@JsonPropertyDescription("Relative time in nanoseconds (to test begin).")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;
    
}
