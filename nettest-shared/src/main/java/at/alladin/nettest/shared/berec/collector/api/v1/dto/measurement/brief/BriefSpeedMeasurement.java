package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Brief/short information of a speed measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Brief/short information of a speed measurement.")
@JsonClassDescription("Brief/short information of a speed measurement.")
public class BriefSpeedMeasurement extends BriefSubMeasurement {

	/**
	 * The calculated (average) download throughput in bits per second.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The calculated (average) download throughput in bits per second.")
	@JsonPropertyDescription("The calculated (average) download throughput in bits per second.")
	@Expose
	@SerializedName("throughput_avg_download_bps")
	@JsonProperty(required = true, value = "throughput_avg_download_bps")
	private Long throughputAvgDownloadBps;

	/**
	 * The calculated (average) upload throughput in bits per second.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The calculated (average) upload throughput in bits per second.")
	@JsonPropertyDescription("The calculated (average) upload throughput in bits per second.")
	@Expose
	@SerializedName("throughput_avg_upload_bps")
	@JsonProperty(required = true, value = "throughput_avg_upload_bps")
	private Long throughputAvgUploadBps;

	/**
	 * Average RTT value in nanoseconds.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Average RTT value in nanoseconds.")
	@JsonPropertyDescription("Average RTT value in nanoseconds.")
	@Expose
	@SerializedName("rtt_average_ns")
	@JsonProperty(required = true, value = "rtt_average_ns")
	private Long rttAverageNs;

	/**
	 * Median RTT value in nanoseconds.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Median RTT value in nanoseconds.")
	@JsonPropertyDescription("Median RTT value in nanoseconds.")
	@Expose
	@SerializedName("rtt_median_ns")
	@JsonProperty(required = true, value = "rtt_median_ns")
	private Long rttMedianNs;

}
