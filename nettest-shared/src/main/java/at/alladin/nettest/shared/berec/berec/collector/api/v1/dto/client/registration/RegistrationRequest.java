package at.alladin.nettest.shared.berec.collector.api.v1.dto.client.registration;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicRequest;

/**
 * Registration request DTO. It contains the fields needed to register a new client.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Registration request DTO. It contains the fields needed to register a new client.")
@JsonClassDescription("Registration request DTO. It contains the fields needed to register a new client.")
public class RegistrationRequest extends BasicRequest {

	/**
	 * Boolean whether the client has accepted the presented terms and conditions.
	 */
	@io.swagger.annotations.ApiModelProperty("Boolean whether the client has accepted the presented terms and conditions.")
	@JsonPropertyDescription("Boolean whether the client has accepted the presented terms and conditions.")
	@Expose
	@SerializedName("terms_and_conditions_accepted")
	@JsonProperty("terms_and_conditions_accepted")
	private Boolean termsAndConditionsAccepted;

	/**
	 * The version of the presented terms and conditions that the client agreed to (or declined).
	 */
	@io.swagger.annotations.ApiModelProperty("The version of the presented terms and conditions that the client agreed to (or declined).")
	@JsonPropertyDescription("The version of the presented terms and conditions that the client agreed to (or declined).")
	@Expose
	@SerializedName("terms_and_conditions_accepted_version")
	@JsonProperty("terms_and_conditions_accepted_version")
	private Integer termsAndConditionsAcceptedVersion;

	/**
	 * The client's group name/identifier. (LMAP: agent.group-id)
	 */
	@io.swagger.annotations.ApiModelProperty("The client's group name/identifier. (LMAP: agent.group-id)")
	@JsonPropertyDescription("The client's group name/identifier. (LMAP: agent.group-id)")
	@Expose
	@SerializedName("group_name")
	@JsonProperty("group_name")
	private String groupName;

	/**
	 *
	 * @return
	 */
	public Boolean getTermsAndConditionsAccepted() {
		return termsAndConditionsAccepted;
	}

	/**
	 *
	 * @param termsAndConditionsAccepted
	 */
	public void setTermsAndConditionsAccepted(Boolean termsAndConditionsAccepted) {
		this.termsAndConditionsAccepted = termsAndConditionsAccepted;
	}

	/**
	 *
	 * @return
	 */
	public Integer getTermsAndConditionsAcceptedVersion() {
		return termsAndConditionsAcceptedVersion;
	}

	/**
	 *
	 * @param termsAndConditionsAcceptedVersion
	 */
	public void setTermsAndConditionsAcceptedVersion(Integer termsAndConditionsAcceptedVersion) {
		this.termsAndConditionsAcceptedVersion = termsAndConditionsAcceptedVersion;
	}

	/**
	 *
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 *
	 * @param groupName
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
