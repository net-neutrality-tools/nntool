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
 * Contains information about the client's OS.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about the client's OS.")
public class OperatingSystemInfo {

	/**
	 * Operating system name (e.g. Android or iOS).
	 */
	@JsonPropertyDescription("Operating system name (e.g. Android or iOS).")
	@Expose
	@SerializedName("name")
	@JsonProperty("name")
	private String name;
	
	/**
	 * Operating system version.
	 */
	@JsonPropertyDescription("Operating system version.")
	@Expose
	@SerializedName("version")
	@JsonProperty("version")
	private String version;
	
	/**
	 * API level of operating system or SDK (e.g. Android API level or Swift SDK version).
	 */
	@JsonPropertyDescription("API level of operating system or SDK (e.g. Android API level or Swift SDK version).")
	@Expose
	@SerializedName("api_level")
	@JsonProperty("api_level")
	private String apiLevel;
	
	/**
	 * CPU usage during the test, if available.
	 */
	@JsonPropertyDescription("CPU usage during the test, if available.")
	@Expose
	@SerializedName("cpu_usage")
	@JsonProperty("cpu_usage")
	private StatisticalTimeSeriesData<Double> cpuUsage;
	
	/**
	 * Memory usage during the test, if available.
	 */
	@JsonPropertyDescription("Memory usage during the test, if available.")
	@Expose
	@SerializedName("mem_usage")
	@JsonProperty("mem_usage")
	private StatisticalTimeSeriesData<Double> memUsage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApiLevel() {
		return apiLevel;
	}

	public void setApiLevel(String apiLevel) {
		this.apiLevel = apiLevel;
	}

	public StatisticalTimeSeriesData<Double> getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(StatisticalTimeSeriesData<Double> cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public StatisticalTimeSeriesData<Double> getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(StatisticalTimeSeriesData<Double> memUsage) {
		this.memUsage = memUsage;
	}
}
