package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This DTO contains speed measurement instructions for the measurement agent.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO contains speed measurement instructions for the measurement agent.")
@JsonClassDescription("This DTO contains speed measurement instructions for the measurement agent.")
@JsonInclude(Include.NON_EMPTY)
public class SpeedMeasurementTypeParameters extends MeasurementTypeParameters {

	/**
	 * @see Durations
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Durations for each phase of the measurement.")
	@JsonPropertyDescription("Durations for each phase of the measurement.")
	@Expose
	@SerializedName("durations")
	@JsonProperty(required = true, value = "durations")
	private Durations durations;
	
	/**
	 * @see Flows
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Number of (TCP) flows for each phase of the measurement.")
	@JsonPropertyDescription("Number of (TCP) flows for each phase of the measurement.")
	@Expose
	@SerializedName("flows")
	@JsonProperty(required = true, value = "flows")
	private Flows flows;
	
	/**
	 * Number of RTT packets that should be send in the RTT measurement.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Number of RTT packets that should be send in the RTT measurement.")
	@JsonPropertyDescription("Number of RTT packets that should be send in the RTT measurement.")
	@Expose
	@SerializedName("rtt_count")
	@JsonProperty(required = true, value = "rtt_count")
	private Integer rttCount;
	
	/**
	 * URL to the measurement code. Overrides the measurement agent's implementation if set.
	 */
	@io.swagger.annotations.ApiModelProperty("URL to the measurement code. Overrides the measurement agent's implementation if set.")
	@JsonPropertyDescription("URL to the measurement code. Overrides the measurement agent's implementation if set.")
	@Expose
	@SerializedName("javascript_measurement_code_url")
	@JsonProperty("javascript_measurement_code_url")
	private String javascriptMeasurementCodeUrl;
	
	/**
	 * The measurement server that should be used, or the first measurement server that should be requested when load balancing.
	 * @see MeasurementServerConfig
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The measurement server that should be used, or the first measurement server that should be requested when load balancing.")
	@JsonPropertyDescription("The measurement server that should be used, or the first measurement server that should be requested when load balancing.")
	@Expose
	@SerializedName("measurement_server")
	@JsonProperty(required = true, value = "measurement_server")
	private MeasurementServerConfig measurementServerConfig;

	/**
	 * Durations for each phase of the measurement.
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel(description = "Durations for each phase of the measurement.")
	@JsonClassDescription("Durations for each phase of the measurement.")
	public static class Durations extends PhaseCountOptions {
		
	}
	
	/**
	 * Number of (TCP) flows for each phase of the measurement.
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel(description = "Number of (TCP) flows for each phase of the measurement.")
	@JsonClassDescription("Number of (TCP) flows for each phase of the measurement.")
	public static class Flows extends PhaseCountOptions {
		
	}
	
	/**
	 * Object that stores an integer value for each speed measurement phase.
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel(description = "Object that stores an integer value for each speed measurement phase.")
	@JsonClassDescription("Object that stores an integer value for each speed measurement phase.")
	@JsonInclude(Include.NON_EMPTY)
	public static class PhaseCountOptions {
		
		/**
		 * Value for the download slow-start phase.
		 */
		@io.swagger.annotations.ApiModelProperty("Value for the download slow-start phase.")
		@JsonPropertyDescription("Value for the download slow-start phase.")
		@Expose
		@SerializedName("download_slow_start")
		@JsonProperty("download_slow_start")
		private Integer downloadSlowStart;
		
		/**
		 * Value for the download phase.
		 */
		@io.swagger.annotations.ApiModelProperty("Value for the download phase.")
		@JsonPropertyDescription("Value for the download phase.")
		@Expose
		@SerializedName("download")
		@JsonProperty("download")
		private Integer download;
		
		/**
		 * Value for the upload slow-start phase.
		 */
		@io.swagger.annotations.ApiModelProperty("Value for the upload slow-start phase.")
		@JsonPropertyDescription("Value for the upload slow-start phase.")
		@Expose
		@SerializedName("upload_slow_start")
		@JsonProperty("upload_slow_start")
		private Integer uploadSlowStart;
		
		/**
		 * Value for the upload phase.
		 */
		@io.swagger.annotations.ApiModelProperty("Value for the upload phase.")
		@JsonPropertyDescription("Value for the upload phase.")
		@Expose
		@SerializedName("upload")
		@JsonProperty("upload")
		private Integer upload;
		
		/**
		 * Value for the RTT phase.
		 */
		@io.swagger.annotations.ApiModelProperty("Value for the RTT phase.")
		@JsonPropertyDescription("Value for the RTT phase.")
		@Expose
		@SerializedName("rtt")
		@JsonProperty("rtt")
		private Integer rtt;

		public Integer getDownloadSlowStart() {
			return downloadSlowStart;
		}

		public void setDownloadSlowStart(Integer downloadSlowStart) {
			this.downloadSlowStart = downloadSlowStart;
		}

		public Integer getDownload() {
			return download;
		}

		public void setDownload(Integer download) {
			this.download = download;
		}

		public Integer getUploadSlowStart() {
			return uploadSlowStart;
		}

		public void setUploadSlowStart(Integer uploadSlowStart) {
			this.uploadSlowStart = uploadSlowStart;
		}

		public Integer getUpload() {
			return upload;
		}

		public void setUpload(Integer upload) {
			this.upload = upload;
		}

		public Integer getRtt() {
			return rtt;
		}

		public void setRtt(Integer rtt) {
			this.rtt = rtt;
		}
	}
	
	/**
	 * Configuration object that holds the measurement server information.
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Configuration object that holds the measurement server information.")
	public static class MeasurementServerConfig {
		
		/**
		 * Measurement server base URL.
		 */
		@io.swagger.annotations.ApiModelProperty("Measurement server base URL.")
		@JsonPropertyDescription("Measurement server base URL.")
		@Expose
		@SerializedName("base_url")
		@JsonProperty("base_url")
		private String baseUrl;

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}
	}

	public Durations getDurations() {
		return durations;
	}

	public void setDurations(Durations durations) {
		this.durations = durations;
	}

	public Flows getFlows() {
		return flows;
	}

	public void setFlows(Flows flows) {
		this.flows = flows;
	}

	public Integer getRttCount() {
		return rttCount;
	}

	public void setRttCount(Integer rttCount) {
		this.rttCount = rttCount;
	}

	public String getJavascriptMeasurementCodeUrl() {
		return javascriptMeasurementCodeUrl;
	}

	public void setJavascriptMeasurementCodeUrl(String javascriptMeasurementCodeUrl) {
		this.javascriptMeasurementCodeUrl = javascriptMeasurementCodeUrl;
	}

	public MeasurementServerConfig getMeasurementServerConfig() {
		return measurementServerConfig;
	}

	public void setMeasurementServerConfig(MeasurementServerConfig measurementServerConfig) {
		this.measurementServerConfig = measurementServerConfig;
	}
}
