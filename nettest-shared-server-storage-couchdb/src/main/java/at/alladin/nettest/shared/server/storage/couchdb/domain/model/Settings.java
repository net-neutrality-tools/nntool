package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

/**
 * Settings for server functionality and clients.
 *
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Settings for server functionality and clients.")
@Document("Settings")
public class Settings {
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	private String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public Settings() {
		docType = DocTypeHelper.getDocType(getClass());
	}

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
	 * The list defining the groups returned in the detail measurement response.
	 */
	@JsonPropertyDescription("The list defining the groups returned in the detail measurement response.")
	@JsonProperty("speedtest_detail_groups")
	@SerializedName("speedtest_detail_groups")
	@Expose
	private List<SpeedtestDetailGroup> speedtestDetailGroups = new ArrayList<>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Urls getUrls() {
		return urls;
	}

	public void setUrls(Urls urls) {
		this.urls = urls;
	}

	public Map<String, SubMeasurementSettings> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Map<String, SubMeasurementSettings> measurements) {
		this.measurements = measurements;
	}
	
	public List<SpeedtestDetailGroup> getSpeedtestDetailGroups() {
		return speedtestDetailGroups;
	}
	
	public void setSpeedtestDetailGroups(List<SpeedtestDetailGroup> speedtestDetailGroups) {
		this.speedtestDetailGroups = speedtestDetailGroups;
	}

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

		public String getControllerService() {
			return controllerService;
		}

		public void setControllerService(String controllerService) {
			this.controllerService = controllerService;
		}

		public String getControllerServiceIpv4() {
			return controllerServiceIpv4;
		}

		public void setControllerServiceIpv4(String controllerServiceIpv4) {
			this.controllerServiceIpv4 = controllerServiceIpv4;
		}

		public String getControllerServiceIpv6() {
			return controllerServiceIpv6;
		}

		public void setControllerServiceIpv6(String controllerServiceIpv6) {
			this.controllerServiceIpv6 = controllerServiceIpv6;
		}

		public String getCollectorService() {
			return collectorService;
		}

		public void setCollectorService(String collectorService) {
			this.collectorService = collectorService;
		}

		public String getMapService() {
			return mapService;
		}

		public void setMapService(String mapService) {
			this.mapService = mapService;
		}

		public String getStatisticService() {
			return statisticService;
		}

		public void setStatisticService(String statisticService) {
			this.statisticService = statisticService;
		}

		public String getWebsite() {
			return website;
		}

		public void setWebsite(String website) {
			this.website = website;
		}
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

		public Integer getNumStreamsDownload() {
			return numStreamsDownload;
		}

		public void setNumStreamsDownload(Integer numStreamsDownload) {
			this.numStreamsDownload = numStreamsDownload;
		}

		public Integer getNumStreamsUpload() {
			return numStreamsUpload;
		}

		public void setNumStreamsUpload(Integer numStreamsUpload) {
			this.numStreamsUpload = numStreamsUpload;
		}

		public Integer getNumPacketsRtt() {
			return numPacketsRtt;
		}

		public void setNumPacketsRtt(Integer numPacketsRtt) {
			this.numPacketsRtt = numPacketsRtt;
		}

		public Integer getDurationUploadSlowStart() {
			return durationUploadSlowStart;
		}

		public void setDurationUploadSlowStart(Integer durationUploadSlowStart) {
			this.durationUploadSlowStart = durationUploadSlowStart;
		}

		public Integer getDurationDownloadSlowStart() {
			return durationDownloadSlowStart;
		}

		public void setDurationDownloadSlowStart(Integer durationDownloadSlowStart) {
			this.durationDownloadSlowStart = durationDownloadSlowStart;
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
