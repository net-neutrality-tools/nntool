package at.alladin.nettest.shared.berec.collector.api.v1.dto.client.settings;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.version.VersionResponse;

/**
 * The settings response object sent to the client.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "The settings response object sent to the client.")
@JsonClassDescription("The settings response object sent to the client.")
public class SettingsResponse extends BasicResponse {

	/**
	 * @see Urls
	 */
	@io.swagger.annotations.ApiModelProperty("Object that contains base URLs for collector, map and statistic services.")
	@JsonPropertyDescription("Object that contains base URLs for collector, map and statistic services.")
	@Expose
	@SerializedName("urls")
	@JsonProperty("urls")
	private Urls urls;

	/**
	 * Map of QoS measurement types to translated type information.
	 */
	@io.swagger.annotations.ApiModelProperty("Map of QoS measurement types to translated type information.")
	@JsonPropertyDescription("Map of QoS measurement types to translated type information.")
	@Expose
	@SerializedName("qos_type_info")
	@JsonProperty("qos_type_info")
	private Map<QoSMeasurementTypeDto, TranslatedQoSTypeInfo> qosTypeInfo;

	/**
	 * @see VersionResponse
	 */
	@io.swagger.annotations.ApiModelProperty("Class for all kind of versions that the server reveals.")
	@JsonPropertyDescription("Class for all kind of versions that the server reveals.")
	@Expose
	@SerializedName("versions")
	@JsonProperty("versions")
	private VersionResponse versions;

	/**
	 * Object that contains base URLs for collector, map and statistic services.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel(description = "Object that contains base URLs for collector, map and statistic services.")
	@JsonClassDescription("Object that contains base URLs for collector, map and statistic services.")
	public static class Urls {

		/**
		 * Base URL of the collector/controller service of the form "[protocol]://[domain]:[port]/[path]".
		 * This domain name should have A and AAAA records.
		 */
		@io.swagger.annotations.ApiModelProperty("Base URL of the collector/controller service of the form \"[protocol]://[domain]:[port]/[path]\". This domain name should have A and AAAA records.")
		@JsonPropertyDescription("Base URL of the collector/controller service of the form \"[protocol]://[domain]:[port]/[path]\". This domain name should have A and AAAA records.")
		@Expose
		@SerializedName("collector_service")
		@JsonProperty("collector_service")
		private String collectorService;

		/**
		 * IPv4-only base URL of the collector/controller service.
		 * This domain name must only have an A record.
		 */
		@io.swagger.annotations.ApiModelProperty("IPv4-only base URL of the collector/controller service. This domain name must only have an A record.")
		@JsonPropertyDescription("IPv4-only base URL of the collector/controller service. This domain name must only have an A record.")
		@Expose
		@SerializedName("collector_service_ipv4")
		@JsonProperty("collector_service_ipv4")
		private String collectorServiceIpv4;

		/**
		 * IPv6-only base URL of the collector/controller service.
		 * This domain name must only have an AAAA record.
		 */
		@io.swagger.annotations.ApiModelProperty("IPv6-only base URL of the collector/controller service. This domain name must only have an AAAA record.")
		@JsonPropertyDescription("IPv6-only base URL of the collector/controller service. This domain name must only have an AAAA record.")
		@Expose
		@SerializedName("collector_service_ipv6")
		@JsonProperty("collector_service_ipv6")
		private String collectorServiceIpv6;

		/**
		 * Base URL of the map service of the form "[protocol]://[domain]:[port]/[path]".
		 */
		@io.swagger.annotations.ApiModelProperty("Base URL of the map service of the form \"[protocol]://[domain]:[port]/[path]\".")
		@JsonPropertyDescription("Base URL of the map service of the form \"[protocol]://[domain]:[port]/[path]\".")
		@Expose
		@SerializedName("map_service")
		@JsonProperty("map_service")
		private String mapService;

		/**
		 * Base URL of the statistic service of the form "[protocol]://[domain]:[port]/[path]".
		 */
		@io.swagger.annotations.ApiModelProperty("Base URL of the statistic service of the form \"[protocol]://[domain]:[port]/[path]\".")
		@JsonPropertyDescription("Base URL of the statistic service of the form \"[protocol]://[domain]:[port]/[path]\".")
		@Expose
		@SerializedName("statistic_service")
		@JsonProperty("statistic_service")
		private String statisticService;

		/**
		 * Base URL of the web site of the form "[protocol]://[domain]:[port]/[path]".
		 */
		@io.swagger.annotations.ApiModelProperty("Base URL of the web site of the form \"[protocol]://[domain]:[port]/[path]\".")
		@JsonPropertyDescription("Base URL of the web site of the form \"[protocol]://[domain]:[port]/[path]\".")
		@Expose
		@SerializedName("website")
		@JsonProperty("website")
		private String website;

	}

	/**
	 * Contains translated information for each available QoS measurement type.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel(description = "Contains translated information for each available QoS measurement type.")
	@JsonClassDescription("Contains translated information for each available QoS measurement type.")
	public static class TranslatedQoSTypeInfo {

		/**
		 * The translated QoS type name.
		 */
		@io.swagger.annotations.ApiModelProperty("The translated QoS type name.")
		@JsonPropertyDescription("The translated QoS type name.")
		@Expose
		@SerializedName("name")
		@JsonProperty("name")
		private String name;

		/**
		 * The translated QoS type description.
		 */
		@io.swagger.annotations.ApiModelProperty("The translated QoS type description.")
		@JsonPropertyDescription("The translated QoS type description.")
		@Expose
		@SerializedName("description")
		@JsonProperty("description")
		private String description;

	}
}
