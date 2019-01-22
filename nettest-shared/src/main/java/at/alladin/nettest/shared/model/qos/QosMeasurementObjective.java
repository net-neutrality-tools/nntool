/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.model.qos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.CouchDbEntity;
import at.alladin.nettest.shared.annotation.ExcludeFromRest;

/**
 * 
 * @author bp
 *
 */
public class QosMeasurementObjective extends CouchDbEntity {

	private static final long serialVersionUID = -6097752007464961243L;

	/**
	 * 
	 */
	@NotNull
	@Expose
    private QosMeasurementType type;
    
	/**
     * 
     */
	@Expose
	@SerializedName("qos_test_uid")
    private int objectiveId;
	
    /**
     * 
     */
	@Expose
    private int measurementClass; // rename enabled
	
	/**
	 * 
	 */
	@Expose
	@ExcludeFromRest
	private String measurementServerUuid;
	
	/**
	 * 
	 */
	@Expose
	private int concurrencyGroup;
    
    /**
     * 
     */
	@Expose
    private Map<String, Object> params = new HashMap<>(); // rename parameters
    
    /**
     * 
     */
	@Expose
    private List<Map<String, String>> results; // rename evaluations
    
    /**
     * 
     */
	@Expose
    private String description; // rename descriptionKey
    
    /**
     * 
     */
	@Expose
    private String summary; // rename summaryKey


	/**
	 * 
	 * @return
	 */
	public QosMeasurementType getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(QosMeasurementType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public int getObjectiveId() {
		return objectiveId;
	}
	
	/**
	 * 
	 * @param objectiveId
	 */
	public void setObjectiveId(int objectiveId) {
		this.objectiveId = objectiveId;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMeasurementClass() {
		return measurementClass;
	}

	/**
	 * 
	 * @param measurementClass
	 */
	public void setMeasurementClass(int measurementClass) {
		this.measurementClass = measurementClass;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMeasurementServerUuid() {
		return measurementServerUuid;
	}
	
	/**
	 * 
	 * @param measurementServerUuid
	 */
	public void setMeasurementServerUuid(String measurementServerUuid) {
		this.measurementServerUuid = measurementServerUuid;
	}

	/**
	 * 
	 * @return
	 */
	public int getConcurrencyGroup() {
		return concurrencyGroup;
	}

	/**
	 * 
	 * @param concurrencyGroup
	 */
	public void setConcurrencyGroup(int concurrencyGroup) {
		this.concurrencyGroup = concurrencyGroup;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * 
	 * @param params
	 */
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Map<String, String>> getResults() {
		return results;
	}
	
	/**
	 * 
	 * @param results
	 */
	public void setResults(List<Map<String, String>> results) {
		this.results = results;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * 
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QosMeasurementObjective [type=" + type + ", objectiveId=" + objectiveId + ", measurementClass="
				+ measurementClass + ", measurementServerUuid=" + measurementServerUuid + ", concurrencyGroup="
				+ concurrencyGroup + ", params=" + params + ", results=" + results + ", description=" + description
				+ ", summary=" + summary + "]";
	}
}
