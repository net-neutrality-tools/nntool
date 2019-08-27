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

package at.alladin.nntool.shared.qos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nntool.shared.qos.util.SipTaskHelper;

/**
 * @author lb@alladin.at
 */
public class SipResult extends AbstractResult {

	@SerializedName(SipTaskHelper.PARAM_OBJECTIVE_PORT)
	@Expose
	private Integer port;

	@SerializedName(SipTaskHelper.PARAM_OBJECTIVE_TIMEOUT)
	@Expose
	private Long timeout;
	
	@SerializedName(SipTaskHelper.PARAM_OBJECTIVE_CALL_DURATION)
	@Expose
	private Long callDuration;

	@SerializedName(SipTaskHelper.PARAM_OBJECTIVE_TO)
	@Expose
	private String objectiveTo;
	
	@SerializedName(SipTaskHelper.PARAM_OBJECTIVE_FROM)
	@Expose
	private String objectivefrom;

	@SerializedName(SipTaskHelper.PARAM_OBJECTIVE_VIA)
	@Expose
	private String objectiveVia;
	
	@SerializedName(SipTaskHelper.PARAM_RESULT_TO)
	@Expose
	private String resultTo;
	
	@SerializedName(SipTaskHelper.PARAM_RESULT_FROM)
	@Expose
	private String resultFrom;

	@SerializedName(SipTaskHelper.PARAM_RESULT_VIA)
	@Expose
	private String resultVia;

	@SerializedName(SipTaskHelper.PARAM_RESULT)
	@Expose
	private String result;
	
	@SerializedName(SipTaskHelper.PARAM_RESULT_CCSR)
	@Expose	
	private Double callCompletionSuccessRate;

	@SerializedName(SipTaskHelper.PARAM_RESULT_CSSR)
	@Expose	
	private Double callSetupSuccessRate;

	@SerializedName(SipTaskHelper.PARAM_RESULT_DCR)
	@Expose	
	private Double callDroppedCallRate;

	/**
	 * 
	 */
	public SipResult() {
		// TODO Auto-generated constructor stub
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Double getCallCompletionSuccessRate() {
		return callCompletionSuccessRate;
	}

	public void setCallCompletionSuccessRate(Double callCompletionSuccessRate) {
		this.callCompletionSuccessRate = callCompletionSuccessRate;
	}

	public Double getCallSetupSuccessRate() {
		return callSetupSuccessRate;
	}

	public void setCallSetupSuccessRate(Double callSetupSuccessRate) {
		this.callSetupSuccessRate = callSetupSuccessRate;
	}

	public Double getCallDroppedCallRate() {
		return callDroppedCallRate;
	}

	public void setCallDroppedCallRate(Double callDroppedCallRate) {
		this.callDroppedCallRate = callDroppedCallRate;
	}
	
	public String getObjectiveTo() {
		return objectiveTo;
	}

	public void setObjectiveTo(String objectiveTo) {
		this.objectiveTo = objectiveTo;
	}

	public String getObjectivefrom() {
		return objectivefrom;
	}

	public void setObjectivefrom(String objectivefrom) {
		this.objectivefrom = objectivefrom;
	}

	public String getResultTo() {
		return resultTo;
	}

	public void setResultTo(String resultTo) {
		this.resultTo = resultTo;
	}

	public String getResultFrom() {
		return resultFrom;
	}

	public void setResultFrom(String resultFrom) {
		this.resultFrom = resultFrom;
	}

	public Long getCallDuration() {
		return callDuration;
	}

	public void setCallDuration(Long callDuration) {
		this.callDuration = callDuration;
	}

	public String getObjectiveVia() {
		return objectiveVia;
	}

	public void setObjectiveVia(String objectiveVia) {
		this.objectiveVia = objectiveVia;
	}

	public String getResultVia() {
		return resultVia;
	}

	public void setResultVia(String resultVia) {
		this.resultVia = resultVia;
	}

	@Override
	public String toString() {
		return "SipResult [port=" + port + ", timeout=" + timeout + ", callDuration=" + callDuration + ", objectiveTo="
				+ objectiveTo + ", objectivefrom=" + objectivefrom + ", objectiveVia=" + objectiveVia + ", resultTo="
				+ resultTo + ", resultFrom=" + resultFrom + ", resultVia=" + resultVia + ", result=" + result
				+ ", callCompletionSuccessRate=" + callCompletionSuccessRate + ", callSetupSuccessRate="
				+ callSetupSuccessRate + ", callDroppedCallRate=" + callDroppedCallRate + "]";
	}
}
