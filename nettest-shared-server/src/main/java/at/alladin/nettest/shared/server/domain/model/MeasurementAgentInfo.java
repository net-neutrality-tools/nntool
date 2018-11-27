package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the measurement agent.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the measurement agent.")
public class MeasurementAgentInfo {

	/**
	 * The agent UUID.
	 */
	@JsonPropertyDescription("The agent UUID.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;
	
	/**
	 * Application version name (e.g. 1.0.0).
	 */
	@JsonPropertyDescription("Application version name (e.g. 1.0.0).")
	@Expose
	@SerializedName("app_version_name")
	@JsonProperty("app_version_name")
	private String appVersionName;
	
	/**
	 * Application version code number (e.g. 10).
	 */
	@JsonPropertyDescription("Application version code number (e.g. 10).")
	@Expose
	@SerializedName("app_version_code")
	@JsonProperty("app_version_code")
	private Integer appVersionCode;
	
	/**
	 * Git revision name.
	 */
	@JsonPropertyDescription("Git revision name.")
	@Expose
	@SerializedName("app_git_revision")
	@JsonProperty("app_git_revision")
	private String appGitRevision;
	
	/**
	 * The agent's language.
	 */
	@JsonPropertyDescription("The agent's language.")
	@Expose
	@SerializedName("language")
	@JsonProperty("language")
	private String language;
	
	/**
	 * The agent's time zone.
	 */
	@JsonPropertyDescription("The agent's time zone.")
	@Expose
	@SerializedName("timezone")
	@JsonProperty("timezone")
	private String timezone;
	
	/**
	 * @see MeasurementAgentType
	 */
	@JsonPropertyDescription("The type of agent.")
    @Expose
	@SerializedName("type")
    @JsonProperty("type")
    private MeasurementAgentType type;
}
