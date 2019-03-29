package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

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

	public Long getThroughputAvgDownloadBps() {
		return throughputAvgDownloadBps;
	}

	public void setThroughputAvgDownloadBps(Long throughputAvgDownloadBps) {
		this.throughputAvgDownloadBps = throughputAvgDownloadBps;
	}

	public Double getThroughputAvgDownloadLog() {
		return throughputAvgDownloadLog;
	}

	public void setThroughputAvgDownloadLog(Double throughputAvgDownloadLog) {
		this.throughputAvgDownloadLog = throughputAvgDownloadLog;
	}

	public Long getThroughputAvgUploadBps() {
		return throughputAvgUploadBps;
	}

	public void setThroughputAvgUploadBps(Long throughputAvgUploadBps) {
		this.throughputAvgUploadBps = throughputAvgUploadBps;
	}

	public Double getThroughputAvgUploadLog() {
		return throughputAvgUploadLog;
	}

	public void setThroughputAvgUploadLog(Double throughputAvgUploadLog) {
		this.throughputAvgUploadLog = throughputAvgUploadLog;
	}

	public Long getBytesDownload() {
		return bytesDownload;
	}

	public void setBytesDownload(Long bytesDownload) {
		this.bytesDownload = bytesDownload;
	}

	public Long getBytesDownloadIncludingSlowStart() {
		return bytesDownloadIncludingSlowStart;
	}

	public void setBytesDownloadIncludingSlowStart(Long bytesDownloadIncludingSlowStart) {
		this.bytesDownloadIncludingSlowStart = bytesDownloadIncludingSlowStart;
	}

	public Long getBytesUpload() {
		return bytesUpload;
	}

	public void setBytesUpload(Long bytesUpload) {
		this.bytesUpload = bytesUpload;
	}

	public Long getBytesUploadIncludingSlowStart() {
		return bytesUploadIncludingSlowStart;
	}

	public void setBytesUploadIncludingSlowStart(Long bytesUploadIncludingSlowStart) {
		this.bytesUploadIncludingSlowStart = bytesUploadIncludingSlowStart;
	}

	public Long getRequestedDurationDownloadSlowStartNs() {
		return requestedDurationDownloadSlowStartNs;
	}

	public void setRequestedDurationDownloadSlowStartNs(Long requestedDurationDownloadSlowStartNs) {
		this.requestedDurationDownloadSlowStartNs = requestedDurationDownloadSlowStartNs;
	}

	public Long getRequestedDurationUploadSlowStartNs() {
		return requestedDurationUploadSlowStartNs;
	}

	public void setRequestedDurationUploadSlowStartNs(Long requestedDurationUploadSlowStartNs) {
		this.requestedDurationUploadSlowStartNs = requestedDurationUploadSlowStartNs;
	}

	public Long getRequestedDurationDownloadNs() {
		return requestedDurationDownloadNs;
	}

	public void setRequestedDurationDownloadNs(Long requestedDurationDownloadNs) {
		this.requestedDurationDownloadNs = requestedDurationDownloadNs;
	}

	public Long getRequestedDurationUploadNs() {
		return requestedDurationUploadNs;
	}

	public void setRequestedDurationUploadNs(Long requestedDurationUploadNs) {
		this.requestedDurationUploadNs = requestedDurationUploadNs;
	}

	public Long getDurationRttNs() {
		return durationRttNs;
	}

	public void setDurationRttNs(Long durationRttNs) {
		this.durationRttNs = durationRttNs;
	}

	public Long getDurationDownloadNs() {
		return durationDownloadNs;
	}

	public void setDurationDownloadNs(Long durationDownloadNs) {
		this.durationDownloadNs = durationDownloadNs;
	}

	public Long getDurationUploadNs() {
		return durationUploadNs;
	}

	public void setDurationUploadNs(Long durationUploadNs) {
		this.durationUploadNs = durationUploadNs;
	}

	public Long getRelativeStartTimeRttNs() {
		return relativeStartTimeRttNs;
	}

	public void setRelativeStartTimeRttNs(Long relativeStartTimeRttNs) {
		this.relativeStartTimeRttNs = relativeStartTimeRttNs;
	}

	public Long getRelativeStartTimeDownloadNs() {
		return relativeStartTimeDownloadNs;
	}

	public void setRelativeStartTimeDownloadNs(Long relativeStartTimeDownloadNs) {
		this.relativeStartTimeDownloadNs = relativeStartTimeDownloadNs;
	}

	public Long getRelativeStartTimeUploadNs() {
		return relativeStartTimeUploadNs;
	}

	public void setRelativeStartTimeUploadNs(Long relativeStartTimeUploadNs) {
		this.relativeStartTimeUploadNs = relativeStartTimeUploadNs;
	}

	public RttInfo getRttInfo() {
		return rttInfo;
	}

	public void setRttInfo(RttInfo rttInfo) {
		this.rttInfo = rttInfo;
	}

	public SpeedMeasurementRawData getSpeedRawData() {
		return speedRawData;
	}

	public void setSpeedRawData(SpeedMeasurementRawData speedRawData) {
		this.speedRawData = speedRawData;
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}
}
