package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information from a single round trip time measurement on the measurement agent.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Contains information from a single round trip time measurement on the measurement agent.")
@JsonClassDescription("Contains information from a single round trip time measurement on the measurement agent.")
public class RttDto {

	/**
	 * Round trip time recorded in nanoseconds.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Round trip time recorded in nanoseconds.")
	@JsonPropertyDescription("Round trip time recorded in nanoseconds.")
	@Expose
	@SerializedName("rtt_ns")
	@JsonProperty(required = true, value = "rtt_ns")
	private Long rttNs;

	/**
     * Relative time in nanoseconds (to test begin).
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Relative time in nanoseconds (to test begin).")
	@JsonPropertyDescription("Relative time in nanoseconds (to test begin).")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty(required = true, value = "relative_time_ns")
    private Long relativeTimeNs;

	/**
     * Average rtt in nanoseconds
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Average rtt in nanoseconds.")
	@JsonPropertyDescription("Average rtt in nanoseconds.")
    @Expose
    @SerializedName("average_ns")
    @JsonProperty(required = true, value = "average_ns")
    private Long averageNs;

	/**
     * Maximum rtt in nanoseconds
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Maximum rtt in nanoseconds.")
	@JsonPropertyDescription("Maximum rtt in nanoseconds.")
    @Expose
    @SerializedName("maximum_ns")
    @JsonProperty(required = true, value = "maximum_ns")
	private Long maximumNs;
	
	/**
     * Median rtt in nanoseconds
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Median rtt in nanoseconds.")
	@JsonPropertyDescription("Median rtt in nanoseconds.")
    @Expose
    @SerializedName("median_ns")
    @JsonProperty(required = true, value = "median_ns")
	private Long medianNs;
		
	/**
     * Minimum rtt in nanoseconds
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Minimum rtt in nanoseconds.")
	@JsonPropertyDescription("Minimum rtt in nanoseconds.")
    @Expose
    @SerializedName("minimum_ns")
    @JsonProperty(required = true, value = "minimum_ns")
	private Long minimumNs;
	
	/**
     * Standard deviation rtt in nanoseconds
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Standard deviation rtt in nanoseconds.")
	@JsonPropertyDescription("Standard deviation rtt in nanoseconds.")
    @Expose
    @SerializedName("standard_deviation_ns")
    @JsonProperty(required = true, value = "standard_deviation_ns")
	private Long standardDeviationNs;
	
		/**
	 * The actual number of sent RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of sent RTT packets.")
	@JsonPropertyDescription("The actual number of sent RTT packets.")
	@Expose
	@SerializedName("num_sent")
	@JsonProperty(required = true, value = "num_sent")
	private Integer numSent;
	
	/**
	 * The actual number of received RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of received RTT packets.")
	@JsonPropertyDescription("The actual number of received RTT packets.")
	@Expose
	@SerializedName("num_received")
	@JsonProperty(required = true, value = "num_received")
	private Integer numReceived;
	
	/**
	 * The actual number of failed RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of failed RTT packets.")
	@JsonPropertyDescription("The actual number of failed RTT packets.")
	@Expose
	@SerializedName("num_error")
	@JsonProperty(required = true, value = "num_error")
	private Integer numError;
	
	/**
	 * The actual number of missing RTT packets.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The actual number of missing RTT packets.")
	@JsonPropertyDescription("The actual number of missing RTT packets.")
	@Expose
	@SerializedName("num_missing")
	@JsonProperty(required = true, value = "num_missing")
	private Integer numMissing;

	/**
	 * The progress of the current rtt result.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The progress of the current rtt result.")
	@JsonPropertyDescription("The progress of the current rtt result.")
	@Expose
	@SerializedName("progress")
	@JsonProperty(required = true, value = "progress")
	private Float progress;

	public Long getRttNs() {
		return rttNs;
	}

	public void setRttNs(Long rttNs) {
		this.rttNs = rttNs;
	}

	public Long getRelativeTimeNs() {
		return relativeTimeNs;
	}

	public void setRelativeTimeNs(Long relativeTimeNs) {
		this.relativeTimeNs = relativeTimeNs;
	}

    public Long getAverageNs() {
        return averageNs;
    }

    public void setAverageNs(Long averageNs) {
        this.averageNs = averageNs;
    }

    public Long getMaximumNs() {
        return maximumNs;
    }

    public void setMaximumNs(Long maximumNs) {
        this.maximumNs = maximumNs;
    }

    public Long getMedianNs() {
        return medianNs;
    }

    public void setMedianNs(Long medianNs) {
        this.medianNs = medianNs;
    }

    public Long getMinimumNs() {
        return minimumNs;
    }

    public void setMinimumNs(Long minimumNs) {
        this.minimumNs = minimumNs;
    }

    public Long getStandardDeviationNs() {
        return standardDeviationNs;
    }

    public void setStandardDeviationNs(Long standardDeviationNs) {
        this.standardDeviationNs = standardDeviationNs;
    }

    public Integer getNumSent() {
        return numSent;
    }

    public void setNumSent(Integer numSent) {
        this.numSent = numSent;
    }

    public Integer getNumReceived() {
        return numReceived;
    }

    public void setNumReceived(Integer numReceived) {
        this.numReceived = numReceived;
    }

    public Integer getNumError() {
        return numError;
    }

    public void setNumError(Integer numError) {
        this.numError = numError;
    }

    public Integer getNumMissing() {
        return numMissing;
    }

    public void setNumMissing(Integer numMissing) {
        this.numMissing = numMissing;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }
}
