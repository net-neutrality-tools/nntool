package at.alladin.nettest.shared.berec.collector.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ClientTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

/**
 * Additional information that is sent by client alongside the request.
 * This contains most information from ClientInfo.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Additional information that is sent by client alongside the request.")
@JsonClassDescription("Additional information that is sent by client alongside the request. This contains most information from ClientInfo.")
public class ApiRequestInfo {

    /**
     * Language specified by the client.
     */
	@io.swagger.annotations.ApiModelProperty("Language specified by the client.")
	@JsonPropertyDescription("Language specified by the client.")
    @Expose
    @SerializedName("language")
    @JsonProperty("language")
    private String language;

	/**
	 * The client's time zone. Is only stored if a measurement is sent to the server.
	 */
	@io.swagger.annotations.ApiModelProperty("The client's time zone. Is only stored if a measurement is sent to the server.")
	@JsonPropertyDescription("The client's time zone. Is only stored if a measurement is sent to the server.")
	@Expose
	@SerializedName("timezone")
	@JsonProperty("timezone")
	private String timezone;

    /**
     * Type of client. Can be one of 'MOBILE', 'BROWSER', 'DESKTOP', 'CLI'.
     */
    @io.swagger.annotations.ApiModelProperty("Type of client.")
    @JsonPropertyDescription("Type of client.")
    @Expose
    @SerializedName("client_type")
    @JsonProperty("client_type")
    private ClientTypeDto clientType;

    /**
	 * The client's UUID. (LMAP: agent.agent-id)
	 * This value is ignored if the resource path already contains the client's UUID.
	 */
    @io.swagger.annotations.ApiModelProperty("The client's UUID. This value is ignored if the resource path already contains the client's UUID.  (LMAP: agent.agent-id)")
    @JsonPropertyDescription("The client's UUID. This value is ignored if the resource path already contains the client's UUID.  (LMAP: agent.agent-id)")
    @Expose
	@SerializedName("client_uuid")
	@JsonProperty("client_uuid")
	private String clientUuid;

    /**
     * Operating system name.
     */
	@io.swagger.annotations.ApiModelProperty("Operating system name.")
	@JsonPropertyDescription("Operating system name.")
    @Expose
    @SerializedName("os_name")
    @JsonProperty("os_name")
    private String osName;

    /**
     * Operating system version.
     */
    @io.swagger.annotations.ApiModelProperty("Operating system version.")
    @JsonPropertyDescription("Operating system version.")
    @Expose
    @SerializedName("os_version")
    @JsonProperty("os_version")
    private String osVersion;

    /**
	 * API level of operating system or SDK (e.g. Android API level or Swift SDK version).
	 */
    @io.swagger.annotations.ApiModelProperty("API level of operating system or SDK (e.g. Android API level or Swift SDK version).")
	@JsonPropertyDescription("API level of operating system or SDK (e.g. Android API level or Swift SDK version).")
	@Expose
	@SerializedName("api_level")
	@JsonProperty("api_level")
	private String apiLevel;

    /**
	 * Device code name.
	 */
    @io.swagger.annotations.ApiModelProperty("Device code name.")
    @JsonPropertyDescription("Device code name.")
	@Expose
	@SerializedName("code_name")
	@JsonProperty("code_name")
	private String codeName;

    /**
     * Detailed device designation.
     */
    @io.swagger.annotations.ApiModelProperty("Detailed device designation.")
    @JsonPropertyDescription("Detailed device designation.")
    @Expose
    @SerializedName("model")
    @JsonProperty("model")
    private String model;

    /**
	 * Application version name (e.g. 1.0.0). (LMAP: part of capabilities.version)
	 */
    @io.swagger.annotations.ApiModelProperty("Application version name (e.g. 1.0.0). (LMAP: part of capabilities.version)")
    @JsonPropertyDescription("Application version name (e.g. 1.0.0). (LMAP: part of capabilities.version)")
	@Expose
	@SerializedName("app_version_name")
	@JsonProperty("app_version_name")
	private String appVersionName;

	/**
	 * Application version code number (e.g. 10). (LMAP: part of capabilities.version)
	 */
    @io.swagger.annotations.ApiModelProperty("Application version code number (e.g. 10). (LMAP: part of capabilities.version)")
    @JsonPropertyDescription("Application version code number (e.g. 10). (LMAP: part of capabilities.version)")
	@Expose
	@SerializedName("app_version_code")
	@JsonProperty("app_version_code")
	private Integer appVersionCode;

	/**
	 * Git revision name. (LMAP: part of capabilities.version)
	 */
    @io.swagger.annotations.ApiModelProperty("Git revision name.")
    @JsonPropertyDescription("Git revision name.")
	@Expose
	@SerializedName("app_git_revision")
	@JsonProperty("app_git_revision")
	private String appGitRevision;

    /**
     * The client device location at the time the request was sent or null if the client doesn't have location information.
     */
    @io.swagger.annotations.ApiModelProperty("The client device location at the time the request was sent or null if the client doesn't have location information.")
    @JsonPropertyDescription("The client device location at the time the request was sent or null if the client doesn't have location information.")
    @Expose
    @SerializedName("geo_location")
    @JsonProperty("geo_location")
    private GeoLocationDto geoLocation;

}
