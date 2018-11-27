package at.alladin.nettest.shared.server.domain.model;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents an agent in the database.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("This class represents an agent in the database.")
public class MeasurementAgent {

	/**
	 * The agent UUID (also serves as primary key).
	 */
	@JsonPropertyDescription("The agent UUID (also serves as primary key).")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;
	
	/**
	 * @see MeasurementAgentType
	 */
	@JsonPropertyDescription("The type of agent.")
    @Expose
	@SerializedName("type")
    @JsonProperty("type")
    private MeasurementAgentType type;
	
	/**
	 * The agent's group name/identifier.
	 */
	@JsonPropertyDescription("The agent's group name/identifier.")
    @Expose
	@SerializedName("group_name")
    @JsonProperty("group_name")
	private String groupName;
    
    /**
     * Time stamp in UTC when the agent was registered.
     */
	@JsonPropertyDescription("Time stamp in UTC when the agent was registered.")
    @Expose
    @SerializedName("registration_time")
	@JsonProperty("registration_time")
    private LocalDateTime registrationTime;
    
    /**
     * Flag if the terms and conditions were accepted by the agent.
     */
	@JsonPropertyDescription("Flag if the terms and conditions were accepted by the agent.")
    @Expose
    @SerializedName("tc_accepted")
	@JsonProperty("tc_accepted")
    private boolean termsAndConditionsAccepted;
    
    /**
     * Version of the terms and conditions that was accepted by the agent.
     */
	@JsonPropertyDescription("Version of the terms and conditions that was accepted by the agent.")
    @Expose
    @SerializedName("tc_accepted_version")
	@JsonProperty("tc_accepted_version")
    private int termsAndConditionsAcceptedVersion;
    
    /**
     * Time stamp when the latest terms and conditions (see termsAndConditionsAcceptedVersion) were accepted by the agent.
     */
	@JsonPropertyDescription("Time stamp when the latest terms and conditions (see termsAndConditionsAcceptedVersion) were accepted by the agent.")
    @Expose
    @SerializedName("tc_accepted_time")
	@JsonProperty("tc_accepted_time")
    private LocalDateTime termsAndConditionsAcceptedTime;
}
