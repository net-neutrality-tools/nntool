/*******************************************************************************
 * Copyright 2013-2019 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author lb
 *
 */
public class UdpTurnResult extends AbstractResult {

	@JsonProperty("udp_turn_objective_port")
	private Long port;
	
	@JsonProperty("udp_turn_objective_timeout")
	private Long timeout;

	@JsonProperty("udp_turn_objective_target")
	private String target;

	@JsonProperty("udp_turn_objective_num_packets")
	private String objectiveNumPackets;

	@JsonProperty("udp_turn_result_rtt_avg_ns")
	private Long rttAvgNs;
	
	@JsonProperty("udp_turn_result_response_num_packets")
	private String responseNumPackets;

	@JsonProperty("udp_turn_result_num_packets")
	private String resultNumPackets;

	@JsonProperty("udp_turn_result_loss_rate")
	private Double packetLossRate;
	
	@JsonProperty("udp_turn_result_delay_standard_deviation_ns")
	private Double delayStandardDeviationNs;
	
	/**
	 *
	 */
	public UdpTurnResult() {
		
	}

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getObjectiveNumPackets() {
		return objectiveNumPackets;
	}

	public void setObjectiveNumPackets(String objectiveNumPackets) {
		this.objectiveNumPackets = objectiveNumPackets;
	}

	public Long getRttAvgNs() {
		return rttAvgNs;
	}

	public void setRttAvgNs(Long rttAvgNs) {
		this.rttAvgNs = rttAvgNs;
	}

	public String getResponseNumPackets() {
		return responseNumPackets;
	}

	public void setResponseNumPackets(String responseNumPackets) {
		this.responseNumPackets = responseNumPackets;
	}

	public String getResultNumPackets() {
		return resultNumPackets;
	}

	public void setResultNumPackets(String resultNumPackets) {
		this.resultNumPackets = resultNumPackets;
	}

	public Double getPacketLossRate() {
		return packetLossRate;
	}

	public void setPacketLossRate(Double packetLossRate) {
		this.packetLossRate = packetLossRate;
	}

	public Double getDelayStandardDeviationNs() {
		return delayStandardDeviationNs;
	}

	public void setDelayStandardDeviationNs(Double delayStandardDeviationNs) {
		this.delayStandardDeviationNs = delayStandardDeviationNs;
	}

	@Override
	public String toString() {
		return "UdpTurnResult [port=" + port + ", timeout=" + timeout + ", target=" + target + ", objectiveNumPackets="
				+ objectiveNumPackets + ", rttAvgNs=" + rttAvgNs + ", responseNumPackets=" + responseNumPackets
				+ ", resultNumPackets=" + resultNumPackets + ", packetLossRate=" + packetLossRate
				+ ", delayStandardDeviationNs=" + delayStandardDeviationNs + "]";
	}
}
