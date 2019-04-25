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

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
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

	public Map<MeasurementTypeDto, SubMeasurementSettings> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Map<String, SubMeasurementSettings> measurements) {
		this.measurements = measurements;
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
	 * @author Lukasz Budryk (alladin-IT GmbH)
	 *
	 */
	@JsonClassDescription("Settings applicable only to conducted speed measurements.")
	public class SpeedMeasurementSettings extends SubMeasurementSettings {

		/**
		 * Contains all measurement class configurations for the upload test.
		 */
		@JsonPropertyDescription("Contains all measurement class configurations for the upload test.")
		@Expose
		@SerializedName("upload")
		@JsonProperty("upload")
		private List<SpeedMeasurementClass> uploadClassList = new ArrayList<>();

		/**
		 * Contains all measurement class configurations for the download test.
		 */
		@JsonPropertyDescription("Contains all measurement class configurations for the download test.")
		@Expose
		@SerializedName("download")
		@JsonProperty("download")
		private List<SpeedMeasurementClass> downloadClassList = new ArrayList<>();

		@Override
		public String toString() {
			return "SpeedMeasurementSettings{" +
					"uploadClassList=" + uploadClassList +
					", downloadClassList=" + downloadClassList +
					"} " + super.toString();
		}

		/**
		 * Holds a single measurement class configuration.
		 *
		 * @author Lukasz Budryk (alladin-IT GmbH)
		 */
		@JsonClassDescription("Holds a single measurement class configuration.")
		public class SpeedMeasurementClass {

			/**
			 *
			 */
			@JsonPropertyDescription("")
			@Expose
			@SerializedName("default")
			@JsonProperty("default")
			private Boolean isDefault = false;

			/**
			 * The requested number of streams for the measurement.
			 */
			@JsonPropertyDescription("The requested number of streams for the measurement.")
			@Expose
			@SerializedName("streams")
			@JsonProperty("streams")
			private Integer numStreams;

			/**
			 * The frame size of the measurement.
			 */
			@JsonPropertyDescription("The frame size of the measurement.")
			@Expose
			@SerializedName("frameSize")
			@JsonProperty("frameSize")
			private Integer frameSize;

			/**
			 * The boundaries for this specific measurement class.
			 */
			@JsonPropertyDescription("The boundaries for this specific measurement class.")
			@Expose
			@SerializedName("bounds")
			@JsonProperty("bounds")
			private Bounds bounds;

			/**
			 * The number of frames sent per upload method call.
			 */
			@JsonPropertyDescription("The number of frames sent per upload method call.")
			@Expose
			@SerializedName("framesPerCall")
			@JsonProperty("framesPerCall")
			private Integer framesPerCall;

			/**
			 *
			 * @author Lukasz Budryk (alladin-IT GmbH)
			 */
			@JsonClassDescription("")
			public class Bounds {

				/**
				 * The lower bound.
				 */
				@JsonPropertyDescription("The lower bound.")
				@Expose
				@SerializedName("lower")
				@JsonProperty("lower")
				private Double lower;

				/**
				 * The upper bound.
				 */
				@JsonPropertyDescription("The upper bound.")
				@Expose
				@SerializedName("upper")
				@JsonProperty("upper")
				private Double upper;

				public Double getLower() {
					return lower;
				}

				public void setLower(Double lower) {
					this.lower = lower;
				}

				public Double getUpper() {
					return upper;
				}

				public void setUpper(Double upper) {
					this.upper = upper;
				}

				@Override
				public String toString() {
					return "Bounds{" +
							"lower=" + lower +
							", upper=" + upper +
							'}';
				}
			}

			public Boolean getDefault() {
				return isDefault;
			}

			public void setDefault(Boolean aDefault) {
				isDefault = aDefault;
			}

			public Integer getNumStreams() {
				return numStreams;
			}

			public void setNumStreams(Integer numStreams) {
				this.numStreams = numStreams;
			}

			public Integer getFrameSize() {
				return frameSize;
			}

			public void setFrameSize(Integer frameSize) {
				this.frameSize = frameSize;
			}

			public Bounds getBounds() {
				return bounds;
			}

			public void setBounds(Bounds bounds) {
				this.bounds = bounds;
			}

			public Integer getFramesPerCall() {
				return framesPerCall;
			}

			public void setFramesPerCall(Integer framesPerCall) {
				this.framesPerCall = framesPerCall;
			}

			@Override
			public String toString() {
				return "SpeedMeasurementClass{" +
						"isDefault=" + isDefault +
						", numStreams=" + numStreams +
						", frameSize=" + frameSize +
						", bounds=" + bounds +
						", framesPerCall=" + framesPerCall +
						'}';
			}
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
