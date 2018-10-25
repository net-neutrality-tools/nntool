package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains speed measurement information.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains speed measurement information.")
public class SpeedMeasurement extends SubMeasurement {
	
	/**
	 * The calculated (average) download throughput in bits per second.
	 */
	@JsonPropertyDescription("The calculated (average) download throughput in bits per second.")
	@Expose
	@SerializedName("throughput_avg_download_bps")
	@JsonProperty("throughput_avg_download_bps")
	private Long throughputAvgDownloadBps;
	
	/**
	 * Common logarithm of the (average) download throughput.
	 */
	@JsonPropertyDescription("Common logarithm of the (average) download throughput.")
	@Expose
	@SerializedName("throughput_avg_download_log")
	@JsonProperty("throughput_avg_download_log")
	private Double throughputAvgDownloadLog;
	
	/**
	 * The calculated (average) upload throughput in bits per second.
	 */
	@JsonPropertyDescription("The calculated (average) upload throughput in bits per second.")
	@Expose
	@SerializedName("throughput_avg_upload_bps")
	@JsonProperty("throughput_avg_upload_bps")
	private Long throughputAvgUploadBps;
	
	/**
	 * Common logarithm of the average upload throughput.
	 */
	@JsonPropertyDescription("Common logarithm of the average upload throughput.")
	@Expose
	@SerializedName("throughput_avg_upload_log")
	@JsonProperty("throughput_avg_upload_log")
	private Double throughputAvgUploadLog;
	
	/**
	 * Bytes received during the speed measurement (Download) without slow-start phase.
	 */
	@JsonPropertyDescription("Bytes received during the speed measurement (Download) without slow-start phase.")
	@Expose
	@SerializedName("bytes_download")
	@JsonProperty("bytes_download")	
	private Long bytesDownload;

	/**
	 * Bytes received during the speed measurement (Download) with slow-start phase.
	 */
	@JsonPropertyDescription("Bytes received during the speed measurement (Download) with slow-start phase.")
	@Expose
	@SerializedName("bytes_download_including_slow_start")
	@JsonProperty("bytes_download_including_slow_start")	
	private Long bytesDownloadIncludingSlowStart;
	
	/**
	 * Bytes transferred during the speed measurement (Upload).
	 */
	@JsonPropertyDescription("Bytes transferred during the speed measurement (Upload).")
	@Expose
    @SerializedName("bytes_upload")
	@JsonProperty("bytes_upload")	
	private Long bytesUpload;
	
	/**
	 * Bytes transferred during the speed measurement (Upload) with slow-start phase.
	 */
	@JsonPropertyDescription("Bytes transferred during the speed measurement (Upload) with slow-start phase.")
	@Expose
	@SerializedName("bytes_upload_including_slow_start")
	@JsonProperty("bytes_upload_including_slow_start")	
	private Long bytesUploadIncludingSlowStart;
	
	/**
	 * The nominal duration for the download slow-start phase.
	 */
	@JsonPropertyDescription("The nominal duration for the download slow-start phase.")
	@Expose
	@SerializedName("requested_duration_download_slow_start_ns")
	@JsonProperty("requested_duration_download_slow_start_ns")
	private Long requestedDurationDownloadSlowStartNs;

	/**
	 * The nominal duration for the upload slow-start phase.
	 */
	@JsonPropertyDescription("The nominal duration for the upload slow-start phase.")
	@Expose
	@SerializedName("requested_duration_upload_slow_start_ns")
	@JsonProperty("requested_duration_upload_slow_start_ns")
	private Long requestedDurationUploadSlowStartNs;

	/**
	 * The nominal measurement duration of the download measurement.
	 */
	@JsonPropertyDescription("The nominal measurement duration of the download measurement.")
	@Expose
	@SerializedName("requested_duration_download_ns")
	@JsonProperty("requested_duration_download_ns")
	private Long requestedDurationDownloadNs;
	
	/**
	 * The nominal measurement duration of the upload measurement.
	 */
	@JsonPropertyDescription("The nominal measurement duration of the upload measurement.")
	@Expose
	@SerializedName("requested_duration_upload_ns")
	@JsonProperty("requested_duration_upload_ns")
	private Long requestedDurationUploadNs;
	
	/**
	 * Duration of the RTT measurement.
	 */
	@JsonPropertyDescription("Duration of the RTT measurement.")
	@Expose
	@SerializedName("duration_rtt_ns")
	@JsonProperty("duration_rtt_ns")
	private Long durationRttNs;
	
	/**
	 * Duration of the download measurement.
	 */
	@JsonPropertyDescription("Duration of the download measurement.")
	@Expose
	@SerializedName("duration_download_ns")
	@JsonProperty("duration_download_ns")
	private Long durationDownloadNs;
	
	/**
	 * Duration of the upload measurement.
	 */
	@JsonPropertyDescription("Duration of the upload measurement.")
	@Expose
	@SerializedName("duration_upload_ns")
	@JsonProperty("duration_upload_ns")
	private Long durationUploadNs;
	
	/**
	 * Relative start time of the RTT measurement in nanoseconds.
	 */
	@JsonPropertyDescription("Relative start time of the RTT measurement in nanoseconds.")
	@Expose
	@SerializedName("relative_start_time_rtt_ns")
	@JsonProperty("relative_start_time_rtt_ns")
	private Long relativeStartTimeRttNs;
	
	/**
	 * Relative start time of the download measurement in nanoseconds.
	 */
	@JsonPropertyDescription("Relative start time of the download measurement in nanoseconds.")
	@Expose
	@SerializedName("relative_start_time_download_ns")
	@JsonProperty("relative_start_time_download_ns")
	private Long relativeStartTimeDownloadNs;

	/**
	 * Relative start time of the upload measurement in nanoseconds.
	 */
	@JsonPropertyDescription("Relative start time of the upload measurement in nanoseconds.")
	@Expose
	@SerializedName("relative_start_time_upload_ns")
	@JsonProperty("relative_start_time_upload_ns")
	private Long relativeStartTimeUploadNs;
	
	/**
	 * @see RttInfo
	 */
	@JsonPropertyDescription("Bytes transferred during the speed measurement (Upload)")
	@Expose
	@SerializedName("rtt_info")
	@JsonProperty("rtt_info")
	private RttInfo rttInfo;
	
	/**
	 * @see SpeedMeasurementRawData
	 */
	@JsonPropertyDescription("Stores the raw data (amount of bytes in time) values for download and upload.")
	@Expose
	@SerializedName("speed_raw_data")
	@JsonProperty("speed_raw_data")
	private SpeedMeasurementRawData speedRawData;
	
	/**
	 * @see ConnectionInfo
	 */
	@JsonPropertyDescription("Contains information about the connection(s) used for the speed measurement.")
	@Expose
	@SerializedName("connection_info")
	@JsonProperty("connection_info")
	private ConnectionInfo connectionInfo;

}
