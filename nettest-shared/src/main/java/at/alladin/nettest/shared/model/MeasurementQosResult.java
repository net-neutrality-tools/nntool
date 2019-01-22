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

package at.alladin.nettest.shared.model;

import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.annotation.ExcludeFromDb;
import at.alladin.nettest.shared.annotation.ExcludeFromRest;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;

/**
 * 
 * @author bp
 *
 */
public class MeasurementQosResult { // rename: QoSResult

	/**
	 * 
	 */
	@Expose
	private int objectiveId;

	/**
	 * 
	 */
	@Expose
	@SerializedName("test_type")
	private QosMeasurementType type;
	
	/**
	 * 
	 */
	@Expose
	@SerializedName("success_count")
	private int successCount;
	
	/**
	 * 
	 */
	@Expose
	@SerializedName("failure_count")
	private int failureCount;
	
	/**
	 * 
	 */
	@Expose
	@ExcludeFromRest
	private boolean implausible; // rm?
	
	/**
	 * 
	 */
	@Expose
	@ExcludeFromRest
	private boolean deleted; // rm
	
	/**
	 * 
	 */
	@Expose
	//@ExcludeFromRest
	// include for liwest raw measurement resource... TODO: improve... (not easy because of http message converter)
	private JsonObject result;
	
	/////////////////////////////
	
	/**
	 * 
	 */
	@Expose
	@SerializedName("test_desc")
	@ExcludeFromDb
	private String testDesc;
	
	/**
	 * 
	 */
	@Expose
	@SerializedName("test_summary")
	@ExcludeFromDb
	private String summary;
	
	/**
	 * 
	 */
	@Expose
	@SerializedName("uid")
	@ExcludeFromDb
	private long oldUid; // rm?
	
	@Expose
	@SerializedName("test_result_key_map")
	@ExcludeFromDb
	private Map<String, String> resultKeyMap;
	
	/////////////////////////////
	
	/**
	 * 
	 */
	public MeasurementQosResult() {
		
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
	public int getSuccessCount() {
		return successCount;
	}

	/**
	 * 
	 * @param successCount
	 */
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	/**
	 * 
	 * @return
	 */
	public int getFailureCount() {
		return failureCount;
	}

	/**
	 * 
	 * @param failureCount
	 */
	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isImplausible() {
		return implausible;
	}

	/**
	 * 
	 * @param implausible
	 */
	public void setImplausible(boolean implausible) {
		this.implausible = implausible;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * 
	 * @param deleted
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/**
	 * 
	 * @return
	 */
	public JsonObject getResult() {
		return result;
	}

	/**
	 * 
	 * @param result
	 */
	public void setResult(JsonObject result) {
		this.result = result;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTestDesc() {
		return testDesc;
	}

	/**
	 * 
	 * @param testDesc
	 */
	public void setTestDesc(String testDesc) {
		this.testDesc = testDesc;
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

	/**
	 * 
	 * @return
	 */
	public long getOldUid() {
		return oldUid;
	}
	
	/**
	 * 
	 * @param oldUid
	 */
	public void setOldUid(long oldUid) {
		this.oldUid = oldUid;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getResultKeyMap() {
		return resultKeyMap;
	}
	
	/**
	 * 
	 * @param resultKeyMap
	 */
	public void setResultKeyMap(Map<String, String> resultKeyMap) {
		this.resultKeyMap = resultKeyMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MeasurementQosResult [objectiveId=" + objectiveId + ", type=" + type + ", successCount=" + successCount
				+ ", failureCount=" + failureCount + ", implausible=" + implausible + ", deleted=" + deleted
				+ ", result=" + result + ", testDesc=" + testDesc + ", summary=" + summary + "]";
	}
}
