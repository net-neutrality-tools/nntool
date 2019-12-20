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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

/**
 * A QoS objective defines one QoS measurement with type, id, concurrency group, parameters, etc.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("A QoS objective defines one QoS measurement with type, id, concurrency group, parameters, etc.")
@Document("QoSMeasurementObjective")
public class QoSMeasurementObjective {

	public QoSMeasurementObjective() {
		docType = DocTypeHelper.getDocType(this.getClass());
	}
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	protected String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	protected String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	protected String docType;
	
	/**
	 * An ID which serves as primary key.
	 */
	@JsonPropertyDescription("An ID which serves as primary key.")
	@Expose
	@SerializedName("objective_id")
	@JsonProperty("objective_id")
	private String objectiveId;
	
	/**
	 * Flag if this QoS measurement objective is enabled.
	 */
	@JsonPropertyDescription("Flag if this QoS measurement objective is enabled.")
	@Expose
	@SerializedName("enabled")
	@JsonProperty("enabled")
	private boolean enabled;

	/**
	 * @see QoSMeasurementType
	 */
	@JsonPropertyDescription("Enum that holds the names of the available QoS measurement objectives.")
	@Expose
	@SerializedName("type")
	@JsonProperty("type")
	private QoSMeasurementType type;
	
	/**
	 * Objectives are ordered based on the concurrency group.
	 * Objectives that have the same concurrency group will be executed in parallel.
	 * Concurrency groups will be executed after each other in sorted order.
	 */
	@JsonPropertyDescription("Objectives are ordered based on the concurrency group. Objectives that have the same concurrency group will be executed in parallel. Concurrency groups will be executed after each other in sorted order.")
	@Expose
	@SerializedName("concurrency_group")
	@JsonProperty("concurrency_group")
	private Long concurrencyGroup;
	
	/**
	 * UUID of the measurement server that is used in the execution of this objective or null if the
	 * objective doesn't require a server.
	 */
	@JsonPropertyDescription("UUID of the measurement server that is used in the execution of this objective or null if the objective doesn't require a server.")
	@Expose
	@SerializedName("measurement_server_uuid")
	@JsonProperty("measurement_server_uuid")
	private String measurementServerUuid;
	
	/**
	 * @see QosTranslationKeys
	 */
	@JsonPropertyDescription("Stores the translation keys for QoS objectives.")
	@Expose
	@SerializedName("translation_keys")
	@JsonProperty("translation_keys")
	private QoSTranslationKeys translationKeys;
	
	/**
	 * A map of QoS objective parameters (e.g. timeout, TCP port, web page URL) 
	 */
	@JsonPropertyDescription("A map of QoS objective parameters (e.g. timeout, TCP port, web page URL) ")
	@Expose
	@SerializedName("parameters")
	@JsonProperty("parameters")
	private Map<String, Object> parameters;
	
	/**
	 * A list of custom evaluations for this QoS objective.
	 * Evaluations are used to present human readable results to the end user.
	 * Example:
	 *  [
	 *   {
	 *     "operator": "eq",
	 *     "on_failure": "ntp.failure",
	 *     "nontransproxy_result_response": "%PARAM nontransproxy_objective_request%",
	 *     "on_success": "ntp.success"
	 *   }
	 * ]
	 */
	@JsonPropertyDescription("A list of custom evaluations for this QoS objective. Evaluations are used to present human readable results to the end user.")
	@Expose
	@SerializedName("evaluations")
	@JsonProperty("evaluations")
	private List<Map<String, Object>> evaluations;
	
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

	public String getObjectiveId() {
		return objectiveId;
	}

	public void setObjectiveId(String objectiveId) {
		this.objectiveId = objectiveId;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public QoSMeasurementType getType() {
		return type;
	}

	public void setType(QoSMeasurementType type) {
		this.type = type;
	}

	public Long getConcurrencyGroup() {
		return concurrencyGroup;
	}

	public void setConcurrencyGroup(Long concurrencyGroup) {
		this.concurrencyGroup = concurrencyGroup;
	}

	public String getMeasurementServerUuid() {
		return measurementServerUuid;
	}

	public void setMeasurementServerUuid(String measurementServerUuid) {
		this.measurementServerUuid = measurementServerUuid;
	}

	public QoSTranslationKeys getTranslationKeys() {
		return translationKeys;
	}

	public void setTranslationKeys(QoSTranslationKeys translationKeys) {
		this.translationKeys = translationKeys;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public List<Map<String, Object>> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(List<Map<String, Object>> evaluations) {
		this.evaluations = evaluations;
	}

	/**
	 * Stores the translation keys for QoS objectives.
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@JsonClassDescription("Stores the translation keys for QoS objectives.")
	public static class QoSTranslationKeys {
		
		/**
		 * Translation key name for the summary of this QoS objective.
		 */
		@JsonPropertyDescription("Translation key name for the summary of this QoS objective.")
		@Expose
		@SerializedName("summary")
		@JsonProperty("summary")
		private String summary;
		
		/**
		 * Translation key name for the description of this QoS objective.
		 */
		@JsonPropertyDescription("Translation key name for the description of this QoS objective.")
		@Expose
		@SerializedName("description")
		@JsonProperty("description")
		private String description;
		
		public String getSummary() {
			return summary;
		}
		
		public void setSummary(String summary) {
			this.summary = summary;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
	}
}
