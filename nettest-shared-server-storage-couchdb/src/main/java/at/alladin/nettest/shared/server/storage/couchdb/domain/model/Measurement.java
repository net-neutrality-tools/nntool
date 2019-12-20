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
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;
import at.alladin.nettest.spring.data.couchdb.core.mapping.Document;

/**
 * Top-Level measurement object that contains sub-measurements (speed, QoS, ...).
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Top-Level measurement object that contains sub-measurements (speed, QoS, ...).")
@Document("Measurement")
public class Measurement {
	
//////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	private String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public Measurement() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRev() {
		return rev;
	}
	
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public String getDocType() {
		return docType;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * The unique identifier (UUIDv4) of the measurement.
	 */
	@JsonPropertyDescription("The unique identifier (UUIDv4) of the measurement.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;

	/**
	 * The open-data identifier (UUIDv4) of the measurement.
	 */
	@JsonPropertyDescription("The open-data identifier (UUIDv4) of the measurement.")
	@Expose
	@SerializedName("open_data_uuid")
	@JsonProperty("open_data_uuid")
	private String openDataUuid;

	/**
	 * A tag provided by the agent.
	 */
	@JsonPropertyDescription("A tag provided by the agent.")
	@Expose
	@SerializedName("tag")
	@JsonProperty("tag")
	private String tag;

	/**
	 * Can contains the result of a Speed and/or QoS measurement.
	 */
	@JsonPropertyDescription("Can contains the result of a Speed and/or QoS measurement.")
	@Expose
	@SerializedName("measurements")
	@JsonProperty("measurements")
	private /*Enum*/Map<MeasurementTypeDto, SubMeasurement> measurements;

	/**
	 * Measurement system uuid. Can be either own system or imported from open-data.
	 */
	@JsonPropertyDescription("Measurement system uuid. Can be either own system or imported from open-data.")
	@Expose
	@SerializedName("system_uuid")
	@JsonProperty("system_uuid")
	private String systemUuid;

	/**
	 * Start Date and time (in UTC) when this measurement result was submitted to the collector.
	 */
	@JsonPropertyDescription("Start Date and time (in UTC) when this measurement result was submitted to the collector.")
	@Expose
	@SerializedName("submit_time")
	@JsonProperty("submit_time")
	private LocalDateTime submitTime;
	
	/**
	 * @see MeasurementTime
	 */
	@JsonPropertyDescription("Start and end time stamps and the duration of a (sub) measurement.")
	@Expose
	@SerializedName("time")
	@JsonProperty("time")
	private MeasurementTime measurementTime;

	/**
	 * @see GeoLocationInfo
	 */
	@JsonPropertyDescription("Contains information about all geographic locations captured during the test.")
	@Expose
	@SerializedName("geo_location_info")
	@JsonProperty("geo_location_info")
	private GeoLocationInfo geoLocationInfo;

	/**
	 * @see MeasurementAgentInfo
	 */
	@JsonPropertyDescription("Contains information about the measurement client.")
	@Expose
	@SerializedName("agent_info")
	@JsonProperty("agent_info")
	private MeasurementAgentInfo agentInfo;

	/**
	 * @see DeviceInfo
	 */
	@JsonPropertyDescription("Contains information about the device the measurement software is running on.")
	@Expose
	@SerializedName("device_info")
	@JsonProperty("device_info")
	private DeviceInfo deviceInfo;

	/**
	 * @see NetworkInfo
	 */
	@JsonPropertyDescription("Contains network related information gathered during the test.")
	@Expose
	@SerializedName("network_info")
	@JsonProperty("network_info")
	private NetworkInfo networkInfo;

	/**
	 * @see QosAdvancedEvaluation
	 */
	@JsonPropertyDescription("Contains advanced QoS related information.")
	@Expose
	@SerializedName("qos_advanced_evaluation")
	@JsonProperty("qos_advanced_evaluation")
	private QosAdvancedEvaluation qosAdvancedEvaluation;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOpenDataUuid() {
		return openDataUuid;
	}

	public void setOpenDataUuid(String openDataUuid) {
		this.openDataUuid = openDataUuid;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public Map<MeasurementTypeDto, SubMeasurement> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Map<MeasurementTypeDto, SubMeasurement> measurements) {
		this.measurements = measurements;
	}

	public String getSystemUuid() {
		return systemUuid;
	}

	public void setSystemUuid(String systemUuid) {
		this.systemUuid = systemUuid;
	}

	public LocalDateTime getSubmitTime() {
		return submitTime;
	}
	
	public void setSubmitTime(LocalDateTime submitTime) {
		this.submitTime = submitTime;
	}
	
	public MeasurementTime getMeasurementTime() {
		return measurementTime;
	}

	public void setMeasurementTime(MeasurementTime measurementTime) {
		this.measurementTime = measurementTime;
	}

	public GeoLocationInfo getGeoLocationInfo() {
		return geoLocationInfo;
	}

	public void setGeoLocationInfo(GeoLocationInfo geoLocationInfo) {
		this.geoLocationInfo = geoLocationInfo;
	}

	public MeasurementAgentInfo getAgentInfo() {
		return agentInfo;
	}

	public void setAgentInfo(MeasurementAgentInfo clientInfo) {
		this.agentInfo = clientInfo;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public NetworkInfo getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(NetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}

	public QosAdvancedEvaluation getQosAdvancedEvaluation() {
		return qosAdvancedEvaluation;
	}

	public void setQosAdvancedEvaluation(QosAdvancedEvaluation qosAdvancedEvaluation) {
		this.qosAdvancedEvaluation = qosAdvancedEvaluation;
	}

	@Override
	public String toString() {
		return "Measurement [rev=" + rev + ", docType=" + docType + ", uuid=" + uuid + ", openDataUuid=" + openDataUuid
				+ ", tag=" + tag + ", measurements=" + measurements + ", systemUuid=" + systemUuid + ", submitTime="
				+ submitTime + ", measurementTime=" + measurementTime + ", geoLocationInfo=" + geoLocationInfo
				+ ", agentInfo=" + agentInfo + ", deviceInfo=" + deviceInfo + ", networkInfo=" + networkInfo + "]";
	}
}
