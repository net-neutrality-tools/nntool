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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains WIFI information.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains WIFI information.")
public class NetworkWifiInfo {

    /**
     * Initial SSID of the network.
     */
	@JsonPropertyDescription("Initial SSID of the network.")
	@Expose
    @SerializedName("initial_ssid")
	@JsonProperty("initial_ssid")
    private String initialSsid;
    
    /**
     * Initial BSSID of the network.
     */
	@JsonPropertyDescription("Initial BSSID of the network.")
	@Expose
    @SerializedName("initial_bssid")
	@JsonProperty("initial_bssid")
    private String initialBssid;
	
	/**
     * Radio frequency of the network.
     */
	@io.swagger.annotations.ApiModelProperty("Radio frequency of the network.")
	@JsonPropertyDescription("Radio frequency of the network.")
	@Expose
    @SerializedName("frequency")
	@JsonProperty("frequency")
	private Integer frequency;
	
	public String getInitialSsid() {
		return initialSsid;
	}
	
	public void setInitialSsid(String initialSsid) {
		this.initialSsid = initialSsid;
	}
	
	public String getInitialBssid() {
		return initialBssid;
	}
	
	public void setInitialBssid(String initialBssid) {
		this.initialBssid = initialBssid;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	
}
