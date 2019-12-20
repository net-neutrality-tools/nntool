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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains network related information gathered during the test.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains network related information gathered during the test.")
public class NetworkInfo {
	
	/**
	 * @see NetworkPointInTime
	 */
	@JsonPropertyDescription("Contains all relevant network information of a single point in time.")
	@Expose
	@SerializedName("network_points_in_time")
	@JsonProperty("network_points_in_time")
	List<NetworkPointInTime> networkPointsInTime;

	/**
	 * @see ComputedNetworkPointInTime
	 */
	@JsonPropertyDescription("Computed network information.")
	@Expose
	@SerializedName("computed_network_info")
	@JsonProperty("computed_network_info")
	ComputedNetworkPointInTime computedNetworkInfo;
	
	/**
	 * @see SignalInfo
	 */
	@JsonPropertyDescription("Contains signal information captured during the test.")
	@Expose
	@SerializedName("signal_info")
	@JsonProperty("signal_info")
	private SignalInfo signalInfo;
	
	/**
	 * Contains true if CGN has been detected, false otherwise.<br> 
	 * CGN detection is done by checking the traceroute test and the respective results.<br> 
	 * According to RFC 6598 (<a href='https://tools.ietf.org/html/rfc6598'>https://tools.ietf.org/html/rfc6598</a>)
	 * the IP address range of CGN is 100.64.0.0/10. 
	 */
	@JsonPropertyDescription("Contains true if CGN has been detected, false otherwise.")
	@Expose
	@SerializedName("is_cgn_detected")
	@JsonProperty("is_cgn_detected")
	private Boolean isCgnDetected = false;

	public List<NetworkPointInTime> getNetworkPointsInTime() {
		return networkPointsInTime;
	}

	public void setNetworkPointsInTime(List<NetworkPointInTime> networkPointsInTime) {
		this.networkPointsInTime = networkPointsInTime;
	}

	public SignalInfo getSignalInfo() {
		return signalInfo;
	}

	public void setSignalInfo(SignalInfo signalInfo) {
		this.signalInfo = signalInfo;
	}

	public ComputedNetworkPointInTime getComputedNetworkInfo() {
		return computedNetworkInfo;
	}

	public void setComputedNetworkInfo(ComputedNetworkPointInTime computedNetworkInfo) {
		this.computedNetworkInfo = computedNetworkInfo;
	}

	public Boolean getIsCgnDetected() {
		return isCgnDetected;
	}

	public void setIsCgnDetected(Boolean isCgnDetected) {
		this.isCgnDetected = isCgnDetected;
	}
}
