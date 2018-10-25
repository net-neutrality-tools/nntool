package at.alladin.nettest.shared.server.domain.model;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class represents a client in the database.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("This class represents a client in the database.")
public class Client {

	/**
	 * The client UUID (also serves as primary key).
	 */
	@JsonPropertyDescription("The client UUID (also serves as primary key).")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;
	
	/**
	 * @see ClientType
	 */
	@JsonPropertyDescription("The type of client.")
    @Expose
	@SerializedName("type")
    @JsonProperty("type")
    private ClientType type;
	
	/**
	 * The client's group name/identifier.
	 */
	@JsonPropertyDescription("The client's group name/identifier.")
    @Expose
	@SerializedName("group_name")
    @JsonProperty("group_name")
	private String groupName;
    
    /**
     * Time stamp in UTC when the client was registered.
     */
	@JsonPropertyDescription("Time stamp in UTC when the client was registered.")
    @Expose
    @SerializedName("registration_time")
	@JsonProperty("registration_time")
    private LocalDateTime registrationTime;
    
    /**
     * Flag if the terms and conditions were accepted by the client.
     */
	@JsonPropertyDescription("Flag if the terms and conditions were accepted by the client.")
    @Expose
    @SerializedName("tc_accepted")
	@JsonProperty("tc_accepted")
    private boolean termsAndConditionsAccepted;
    
    /**
     * Version of the terms and conditions that was accepted by the client.
     */
	@JsonPropertyDescription("Version of the terms and conditions that was accepted by the client.")
    @Expose
    @SerializedName("tc_accepted_version")
	@JsonProperty("tc_accepted_version")
    private int termsAndConditionsAcceptedVersion;
    
    /**
     * Time stamp when the latest terms and conditions (see termsAndConditionsAcceptedVersion) were accepted by the client.
     */
	@JsonPropertyDescription("Time stamp when the latest terms and conditions (see termsAndConditionsAcceptedVersion) were accepted by the client.")
    @Expose
    @SerializedName("tc_accepted_time")
	@JsonProperty("tc_accepted_time")
    private LocalDateTime termsAndConditionsAcceptedTime;
}
