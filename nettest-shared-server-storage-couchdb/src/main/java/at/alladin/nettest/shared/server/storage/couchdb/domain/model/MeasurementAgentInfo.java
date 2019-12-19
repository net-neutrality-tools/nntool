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

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getAppVersionName() {
		return appVersionName;
	}

	public void setAppVersionName(String appVersionName) {
		this.appVersionName = appVersionName;
	}

	public Integer getAppVersionCode() {
		return appVersionCode;
	}

	public void setAppVersionCode(Integer appVersionCode) {
		this.appVersionCode = appVersionCode;
	}

	public String getAppGitRevision() {
		return appGitRevision;
	}

	public void setAppGitRevision(String appGitRevision) {
		this.appGitRevision = appGitRevision;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public MeasurementAgentType getType() {
		return type;
	}

	public void setType(MeasurementAgentType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MeasurementAgentInfo [uuid=" + uuid + ", appVersionName=" + appVersionName + ", appVersionCode="
				+ appVersionCode + ", appGitRevision=" + appGitRevision + ", language=" + language + ", timezone="
				+ timezone + ", type=" + type + "]";
	}
}
