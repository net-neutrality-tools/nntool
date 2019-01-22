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

package at.alladin.nettest.shared.model.response;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import at.alladin.nettest.shared.model.MeasurementQosResult;

/**
 * 
 * @author lb
 *
 */
public class QosMeasurementResultResponse {

	/**
	 * 
	 */
	@Expose
	private String evaluation; // rm
	
	/**
	 * 
	 */
	@SerializedName("eval_times")
	@Expose
	private Map<String, Object> evalTimes; // rm
	
	/**
	 * 
	 */
	@SerializedName("testresultdetail")
	@Expose
	private List<MeasurementQosResult> testResultDetail; // mv EvaluatedQoSResult
	
	/**
	 * 
	 */
	@SerializedName("testresultdetail_desc")
	@Expose
	private List<Map<String, Object>> testResultDetailDescription;
	
	/**
	 * 
	 */
	@SerializedName("testresultdetail_testdesc")
	@Expose
	private List<Map<String, Object>> testResultDetailTestDescription;
	
	/**
	 * 
	 */
	public QosMeasurementResultResponse() {
		
	}

	/**
	 * 
	 * @return
	 */
	public String getEvaluation() {
		return evaluation;
	}

	/**
	 * 
	 * @param evaluation
	 */
	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getEvalTimes() {
		return evalTimes;
	}

	/**
	 * 
	 * @param evalTimes
	 */
	public void setEvalTimes(Map<String, Object> evalTimes) {
		this.evalTimes = evalTimes;
	}

	/**
	 * 
	 * @return
	 */
	public List<MeasurementQosResult> getTestResultDetail() {
		return testResultDetail;
	}

	/**
	 * 
	 * @param testResultDetail
	 */
	public void setTestResultDetail(List<MeasurementQosResult> testResultDetail) {
		this.testResultDetail = testResultDetail;
	}

	/**
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getTestResultDetailDescription() {
		return testResultDetailDescription;
	}

	/**
	 * 
	 * @param testResultDetailDescription
	 */
	public void setTestResultDetailDescription(List<Map<String, Object>> testResultDetailDescription) {
		this.testResultDetailDescription = testResultDetailDescription;
	}

	/**
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getTestResultDetailTestDescription() {
		return testResultDetailTestDescription;
	}

	/**
	 * 
	 * @param testResultDetailTestDescription
	 */
	public void setTestResultDetailTestDescription(List<Map<String, Object>> testResultDetailTestDescription) {
		this.testResultDetailTestDescription = testResultDetailTestDescription;
	}
}
