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

/**
 * @author lb@alladin.at
 */
public class SipResult extends AbstractResult {

	@SerializedName("sip_objective_port")
	@Expose
	private Integer port;

	@SerializedName("sip_objective_timeout")
	@Expose
	private Long timeout;
	
	@SerializedName("sip_objective_call_duration")
	@Expose
	private Long callDuration;

	@SerializedName("sip_objective_to")
	@Expose
	private String objectiveTo;
	
	@SerializedName("sip_objective_from")
	@Expose
	private String objectivefrom;

	@SerializedName("sip_objective_via")
	@Expose
	private String objectiveVia;
	
	@SerializedName("sip_result_to")
	@Expose
	private String resultTo;
	
	@SerializedName("sip_result_from")
	@Expose
	private String resultFrom;

	@SerializedName("sip_result_via")
	@Expose
	private String resultVia;

	@SerializedName("sip_result")
	@Expose
	private String result;
	
	@SerializedName("sip_result_ccsr")
	@Expose	
	private Double callCompletionSuccessRate;

	@SerializedName("sip_result_cssr")
	@Expose	
	private Double callSetupSuccessRate;

	@SerializedName("sip_result_dcr")
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
