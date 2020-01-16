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

package at.alladin.nettest.service.loadbalancing.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class LoadApiResponse {

	@JsonProperty("cpu_avg")
	Map<String, String> cpuLoad;
	
	@JsonProperty("mem_bytes")
	LoadApiMem memLoad;
	
	@JsonProperty("rx_rates")
	LoadApiRates rxRates;
	
	@JsonProperty("tx_rates")
	LoadApiRates txRates;

	@JsonProperty("overloaded")
	Boolean isOverloaded;
	
	@JsonProperty("timestamp")
	String timestamp;
	
	@JsonProperty("timezone")
	String timezone;
	
	public Map<String, String> getCpuLoad() {
		return cpuLoad;
	}

	public void setCpuLoad(Map<String, String> cpuLoad) {
		this.cpuLoad = cpuLoad;
	}

	public LoadApiMem getMemLoad() {
		return memLoad;
	}

	public void setMemLoad(LoadApiMem memLoad) {
		this.memLoad = memLoad;
	}

	public LoadApiRates getRxRates() {
		return rxRates;
	}

	public void setRxRates(LoadApiRates rxRates) {
		this.rxRates = rxRates;
	}

	public LoadApiRates getTxRates() {
		return txRates;
	}

	public void setTxRates(LoadApiRates txRates) {
		this.txRates = txRates;
	}

	public Boolean getIsOverloaded() {
		return isOverloaded;
	}

	public void setIsOverloaded(Boolean isOverloaded) {
		this.isOverloaded = isOverloaded;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
}
