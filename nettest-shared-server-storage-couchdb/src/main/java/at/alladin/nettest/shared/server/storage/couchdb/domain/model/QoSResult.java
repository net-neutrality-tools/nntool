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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about the result of a single QoS test.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the result of a single QoS test.")
public class QoSResult {

	/**
	 * A reference to the QoS objective ID.
	 */
	@JsonPropertyDescription("A reference to the QoS objective ID.")
	@Expose
	@SerializedName("objective_id")
	@JsonProperty("objective_id")
	private Long objectiveId;

	/**
	 * The type of the given QoSResult (e.g. TCP, UDP).
	 * @see QoSMeasurementType
	 */
	@JsonPropertyDescription("The type of the given QoSResult (e.g. TCP, UDP).")
	@Expose
	@SerializedName("type")
	@JsonProperty("type")
	private QoSMeasurementType type;

	/**
	 * Stores the result key-value pairs gathered from the QoS measurement execution.
	 */
	@JsonPropertyDescription("Stores the result key-value pairs gathered from the QoS measurement execution.")
	@Expose
	@SerializedName("results")
	@JsonProperty("results")
	private Map<String, Object> results;

	/**
	 * The count of positive evaluations (successes).
	 */
	@JsonPropertyDescription("The count of positive evalutations (successes).")
	@Expose
	@SerializedName("success_count")
	@JsonProperty("success_count")
	private Integer successCount;

	/**
	 * The count of negative evaluations (failures).
	 */
	@JsonPropertyDescription("The count of negative evalutations (failures).")
	@Expose
	@SerializedName("failure_count")
	@JsonProperty("failure_count")
	private Integer failureCount;

	/**
	 * Flag to mark this QoS measurement as implausible.
	 */
	@JsonPropertyDescription("Flag to mark this QoS measurement as implausible")
	@Expose
	@SerializedName("implausible")
	@JsonProperty("implausible")
	private boolean implausible;

	public Long getObjectiveId() {
		return objectiveId;
	}

	public void setObjectiveId(Long objectiveId) {
		this.objectiveId = objectiveId;
	}

	public QoSMeasurementType getType() {
		return type;
	}

	public void setType(QoSMeasurementType type) {
		this.type = type;
	}

	public Map<String, Object> getResults() {
		return results;
	}

	public void setResults(Map<String, Object> results) {
		this.results = results;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer succesCount) {
		this.successCount = succesCount;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public boolean isImplausible() {
		return implausible;
	}

	public void setImplausible(boolean implausible) {
		this.implausible = implausible;
	}

	@Override
	public String toString() {
		return "QoSResult [objectiveId=" + objectiveId + ", type=" + type + ", results=" + results + ", successCount="
				+ successCount + ", failureCount=" + failureCount + ", implausible=" + implausible + "]";
	}
	
}
