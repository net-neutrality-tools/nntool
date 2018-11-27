package at.alladin.nettest.shared.server.domain.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Settings for server functionality and clients.
 *
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Settings for server functionality and clients.")
public class Settings {

	/**
	 * A UUID which serves as primary key.
	 */
	@JsonPropertyDescription("A UUID which serves as primary key.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;

	/**
	 * Contains the base URLs for the various services accessed by the client.
	 * @see Urls
	 */
	@JsonPropertyDescription("Contains the base URLs for the various services accessed by the client.")
	@Expose
	@SerializedName("urls")
	@JsonProperty("urls")
	private Urls urls;

	/**
	 * This map contains settings per measurement type.
	 */
	@JsonPropertyDescription("This map contains settings per measurement type.")
	@Expose
	@SerializedName("measurements")
	@JsonProperty("measurements")
	private Map<String, SubMeasurementSettings> measurements;

	/**
	 * Object that contains base URLs for controller, collector, map and statistic services.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Object that contains base URLs for controller, collector, map and statistic services.")
	public static class Urls {

		/**
		 * Base URL of the controller service of the form "[protocol]://[domain]:[port]/[path]".
		 * This domain name should have A and AAAA records.
		 */
		@io.swagger.annotations.ApiModelProperty("Base URL of the controller service of the form \"[protocol]://[domain]:[port]/[path]\". This domain name should have A and AAAA records.")
		@JsonPropertyDescription("Base URL of the controller service of the form \"[protocol]://[domain]:[port]/[path]\". This domain name should have A and AAAA records.")
		@Expose
		@SerializedName("controller_service")
		@JsonProperty("controller_service")
		private String controllerService;
		
		/**
		 * IPv4-only base URL of the controller service.
		 * This domain name must only have an A record.
		 */
		@io.swagger.annotations.ApiModelProperty("IPv4-only base URL of the controller service. This domain name must only have an A record.")
		@JsonPropertyDescription("IPv4-only base URL of the controller service. This domain name must only have an A record.")
		@Expose
		@SerializedName("controller_service_ipv4")
		@JsonProperty("controller_service_ipv4")
		private String controllerServiceIpv4;
		
		/**
		 * IPv6-only base URL of the controller service.
		 * This domain name must only have an AAAA record.
		 */
		@io.swagger.annotations.ApiModelProperty("IPv6-only base URL of the controller service. This domain name must only have an AAAA record.")
		@JsonPropertyDescription("IPv6-only base URL of the controller service. This domain name must only have an AAAA record.")
		@Expose
		@SerializedName("controller_service_ipv6")
		@JsonProperty("controller_service_ipv6")
		private String controllerServiceIpv6;
		
		/**
		 * Base URL of the collector service of the form "[protocol]://[domain]:[port]/[path]".
		 * This domain name should have A and AAAA records.
		 */
		@io.swagger.annotations.ApiModelProperty("Base URL of the collector service of the form \"[protocol]://[domain]:[port]/[path]\". This domain name should have A and AAAA records.")
		@JsonPropertyDescription("Base URL of the collector service of the form \"[protocol]://[domain]:[port]/[path]\". This domain name should have A and AAAA records.")
		@Expose
		@SerializedName("collector_service")
		@JsonProperty("collector_service")
		private String collectorService;

		/**
		 * Base URL of the map service of the form "[protocol]://[domain]:[port]/[path]".
		 */
		@JsonPropertyDescription("Base URL of the map service of the form \"[protocol]://[domain]:[port]/[path]\".")
		@Expose
		@SerializedName("map_service")
		@JsonProperty("map_service")
		private String mapService;

		/**
		 * Base URL of the statistic service of the form "[protocol]://[domain]:[port]/[path]".
		 */
		@JsonPropertyDescription("Base URL of the statistic service of the form \"[protocol]://[domain]:[port]/[path]\".")
		@Expose
		@SerializedName("statistic_service")
		@JsonProperty("statistic_service")
		private String statisticService;

		/**
		 * Base URL of the web site of the form "[protocol]://[domain]:[port]/[path]".
		 */
		@JsonPropertyDescription("Base URL of the web site of the form \"[protocol]://[domain]:[port]/[path]\".")
		@Expose
		@SerializedName("website")
		@JsonProperty("website")
		private String website;
	}

	/**
	 * Common settings for all sub measurements (i.e. speed measurements and QoS measurements).
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Common settings for all sub measurements (i.e. speed measurements and QoS measurements).")
	public static class SubMeasurementSettings {

	}

	/**
	 * Settings applicable only to conducted speed measurements.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Settings applicable only to conducted speed measurements.")
	public class SpeedMeasurementSettings extends SubMeasurementSettings {

		/**
		 * The requested number of streams for the download measurement.
		 */
		@JsonPropertyDescription("The requested number of streams for the download measurement.")
		@Expose
		@SerializedName("num_streams_download")
		@JsonProperty("num_streams_download")
		private Integer numStreamsDownload;

		/**
		 * The requested number of streams for the upload measurement.
		 */
		@JsonPropertyDescription("The requested number of streams for the upload measurement.")
		@Expose
		@SerializedName("num_streams_upload")
		@JsonProperty("num_streams_upload")
		private Integer numStreamsUpload;

		/**
		 * The requested number of packets to send during the RTT measurement.
		 */
		@JsonPropertyDescription("The requested number of packets to send during the RTT measurement.")
		@Expose
		@SerializedName("num_packets_rtt")
		@JsonProperty("num_packets_rtt")
		private Integer numPacketsRtt;

		/**
		 * The nominal duration for the upload slow-start phase.
		 */
		@io.swagger.annotations.ApiModelProperty("The nominal duration for the upload slow-start phase.")
		@JsonPropertyDescription("The nominal duration for the upload slow-start phase.")
		@Expose
		@SerializedName("duration_upload_slow_start")
		@JsonProperty("duration_upload_slow_start")
		private Integer durationUploadSlowStart;

		/**
		 * The nominal duration for the download slow-start phase.
		 */
		@io.swagger.annotations.ApiModelProperty("The nominal duration for the download slow-start phase.")
		@JsonPropertyDescription("The nominal duration for the download slow-start phase.")
		@Expose
		@SerializedName("duration_download_slow_start")
		@JsonProperty("duration_download_slow_start")
		private Integer durationDownloadSlowStart;

		/**
		 * The nominal measurement duration of the download measurement.
		 */
		@JsonPropertyDescription("The nominal measurement duration of the download measurement.")
		@Expose
		@SerializedName("duration_download_ns")
		@JsonProperty("duration_download_ns")
		private Long durationDownloadNs;

		/**
		 * The nominal measurement duration of the upload measurement.
		 */
		@JsonPropertyDescription("The nominal measurement duration of the upload measurement.")
		@Expose
		@SerializedName("duration_upload_ns")
		@JsonProperty("duration_upload_ns")
		private Long durationUploadNs;
	}

	/**
	 * Settings applicable only to conducted Quality of Service (QoS) measurements.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Settings applicable only to conducted Quality of Service (QoS) measurements.")
	public class QoSMeasurementSettings extends SubMeasurementSettings {

	}
}
