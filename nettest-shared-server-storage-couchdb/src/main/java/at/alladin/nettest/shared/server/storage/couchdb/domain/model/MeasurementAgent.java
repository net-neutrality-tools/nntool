/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

/**
 * This class represents an agent in the database.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("This class represents an agent in the database.")
@Document("MeasurementAgent")
public class MeasurementAgent {

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
	
	public MeasurementAgent() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
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

	public MeasurementAgentType getType() {
		return type;
	}

	public void setType(MeasurementAgentType type) {
		this.type = type;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public LocalDateTime getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(LocalDateTime registrationTime) {
		this.registrationTime = registrationTime;
	}

	public boolean isTermsAndConditionsAccepted() {
		return termsAndConditionsAccepted;
	}

	public void setTermsAndConditionsAccepted(boolean termsAndConditionsAccepted) {
		this.termsAndConditionsAccepted = termsAndConditionsAccepted;
	}

	public int getTermsAndConditionsAcceptedVersion() {
		return termsAndConditionsAcceptedVersion;
	}

	public void setTermsAndConditionsAcceptedVersion(int termsAndConditionsAcceptedVersion) {
		this.termsAndConditionsAcceptedVersion = termsAndConditionsAcceptedVersion;
	}

	public LocalDateTime getTermsAndConditionsAcceptedTime() {
		return termsAndConditionsAcceptedTime;
	}

	public void setTermsAndConditionsAcceptedTime(LocalDateTime termsAndConditionsAcceptedTime) {
		this.termsAndConditionsAcceptedTime = termsAndConditionsAcceptedTime;
	}
	
}
