package at.alladin.nettest.shared.server.domain.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;

/**
 * Top-Level measurement object that contains sub-measurements (speed, QoS, ...).
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Top-Level measurement object that contains sub-measurements (speed, QoS, ...).")
public class Measurement {

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
	 * A tag provided by the client.
	 */
	@JsonPropertyDescription("A tag provided by the client.")
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
	 * Measurement source. Can be either own system or imported from open-data.
	 */
	@JsonPropertyDescription("Measurement source. Can be either own system or imported from open-data.")
	@Expose
	@SerializedName("source")
	@JsonProperty("source")
	private String source;

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
	@SerializedName("client_info")
	@JsonProperty("client_info")
	private MeasurementAgentInfo clientInfo;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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

	public MeasurementAgentInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(MeasurementAgentInfo clientInfo) {
		this.clientInfo = clientInfo;
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
}
