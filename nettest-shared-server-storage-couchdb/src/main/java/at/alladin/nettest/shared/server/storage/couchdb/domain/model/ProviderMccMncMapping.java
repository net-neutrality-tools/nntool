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

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * MCC/MNC mapping configuration used to identify a mobile provider.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("MCC/MNC mapping configuration used to identify a mobile provider.")
public class ProviderMccMncMapping {

	/**
	 * The MCC/MNC of the SIM.
	 */
	@JsonPropertyDescription("The MCC/MNC of the SIM.")
	@Expose
	@SerializedName("sim_mcc_mnc")
	@JsonProperty("sim_mcc_mnc")
	private MccMnc simMccMnc;
	
	/**
	 * The MCC/MNC of the network.
	 */
	@JsonPropertyDescription("The MCC/MNC of the network.")
	@Expose
	@SerializedName("network_mcc_mnc")
	@JsonProperty("network_mcc_mnc")
	private MccMnc networkMccMnc;
	
	/**
	 * Optional condition used to set the date this MCC/MNC mapping is valid from.
	 */
	@JsonPropertyDescription("Optional condition used to set the date this MCC/MNC mapping is valid from.")
	@Expose
	@SerializedName("condition_valid_from")
	@JsonProperty("condition_valid_from")
	private LocalDateTime conditionValidFrom;
	
	/**
	 * Optional condition used to set the date this MCC/MNC mapping is valid to.
	 */
	@JsonPropertyDescription("Optional condition used to set the date this MCC/MNC mapping is valid to.")
	@Expose
	@SerializedName("condition_valid_to")
	@JsonProperty("condition_valid_to")
	private LocalDateTime conditionValidTo;

	public MccMnc getSimMccMnc() {
		return simMccMnc;
	}

	public void setSimMccMnc(MccMnc simMccMnc) {
		this.simMccMnc = simMccMnc;
	}

	public MccMnc getNetworkMccMnc() {
		return networkMccMnc;
	}

	public void setNetworkMccMnc(MccMnc networkMccMnc) {
		this.networkMccMnc = networkMccMnc;
	}

	public LocalDateTime getConditionValidFrom() {
		return conditionValidFrom;
	}

	public void setConditionValidFrom(LocalDateTime conditionValidFrom) {
		this.conditionValidFrom = conditionValidFrom;
	}

	public LocalDateTime getConditionValidTo() {
		return conditionValidTo;
	}

	public void setConditionValidTo(LocalDateTime conditionValidTo) {
		this.conditionValidTo = conditionValidTo;
	}
}
