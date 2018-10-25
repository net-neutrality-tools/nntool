package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ClientInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;

/**
 * This DTO class contains all measurement information that is sent to the client.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO class contains all measurement information that is sent to the client.")
@JsonClassDescription("This DTO class contains all measurement information that is sent to the client.")
public class FullMeasurementResponse extends BasicResponse {

	/**
	 * The unique identifier (UUIDv4) of the measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("The unique identifier (UUIDv4) of the measurement.")
	@JsonPropertyDescription("The unique identifier (UUIDv4) of the measurement.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;
	
	/**
	 * The open-data identifier (UUIDv4) of the measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("The open-data identifier (UUIDv4) of the measurement.")
	@JsonPropertyDescription("The open-data identifier (UUIDv4) of the measurement.")
	@Expose
	@SerializedName("open_data_uuid")
	@JsonProperty("open_data_uuid")
	private String openDataUuid;
	
	/**
	 * Measurement source. Can be either own system or imported from open-data.
	 */
	@io.swagger.annotations.ApiModelProperty("Measurement source. Can be either own system or imported from open-data.")
	@JsonPropertyDescription("Measurement source. Can be either own system or imported from open-data.")
	@Expose
	@SerializedName("source")
	@JsonProperty("source")
	private String source;
	
	/**
	 * Contains the result of a Speed and/or QoS measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains the result of a Speed and/or QoS measurement.")
	@JsonPropertyDescription("Contains the result of a Speed and/or QoS measurement.")
	@Expose
	@SerializedName("measurements")
	@JsonProperty("measurements")
	private /*Enum*/Map<MeasurementType, FullSubMeasurement> measurements;
	
// MeasurementTime
	
	/**
	 * Start Date and time for this (sub-) measurement. Date and time is always stored as UTC.
	 */
	@io.swagger.annotations.ApiModelProperty("Start Date and time for this (sub-) measurement.")
	@JsonPropertyDescription("Start Date and time for this (sub-) measurement.")
	@Expose
	@SerializedName("start_time")
	@JsonProperty("start_time")
	private LocalDateTime startTime;
	
	/**
	 * End Date and time for this (sub-) measurement. Date and time is always stored as UTC.
	 */
	@io.swagger.annotations.ApiModelProperty("End Date and time for this (sub-) measurement. Date and time is always stored as UTC.")
	@JsonPropertyDescription("End Date and time for this (sub-) measurement. Date and time is always stored as UTC.")
	@Expose
	@SerializedName("end_time")
	@JsonProperty("end_time")
	private LocalDateTime endTime;
	
	/**
	 * Duration of a measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("Duration of a measurement.")
	@JsonPropertyDescription("Duration of a measurement.")
	@Expose
	@SerializedName("duration_ns")
	@JsonProperty("duration_ns")
	private Long durationNs;
	
// GeoLocationInfo

	/**
	 * List of all captured geographic locations.
	 */
	@io.swagger.annotations.ApiModelProperty("List of all captured geographic locations.")
	@JsonPropertyDescription("List of all captured geographic locations.")
	@Expose
	@SerializedName("geo_locations")
	@JsonProperty("geo_locations")
	private List<GeoLocationDto> geoLocations;
	
// ClientInfo
	
	/**
	 * @see ClientInfoDto
	 */
	@io.swagger.annotations.ApiModelProperty("Contains information about the measurement client.")
	@JsonPropertyDescription("Contains information about the measurement client.")
	@Expose
	@SerializedName("client_info")
	@JsonProperty("client_info")
	private ClientInfoDto clientInfo;
	
// DeviceInfo
	
	/**
	 * @see DeviceInfoDto
	 */
	@io.swagger.annotations.ApiModelProperty("Contains information about the device the measurement software is running on.")
	@JsonPropertyDescription("Contains information about the device the measurement software is running on.")
	@Expose
	@SerializedName("device_info")
	@JsonProperty("device_info")
	private DeviceInfoDto deviceInfo;
	
// NetworkInfo
	
	/**
	 * @see NetworkInfoDto
	 */
	@io.swagger.annotations.ApiModelProperty("Contains network related information gathered during the test.")
	@JsonPropertyDescription("Contains network related information gathered during the test.")
	@Expose
	@SerializedName("network_info")
	@JsonProperty("network_info")
	private NetworkInfoDto networkInfo;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<MeasurementType, FullSubMeasurement> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Map<MeasurementType, FullSubMeasurement> measurements) {
		this.measurements = measurements;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Long getDurationNs() {
		return durationNs;
	}

	public void setDurationNs(Long durationNs) {
		this.durationNs = durationNs;
	}

	public List<GeoLocationDto> getGeoLocations() {
		return geoLocations;
	}

	public void setGeoLocations(List<GeoLocationDto> geoLocations) {
		this.geoLocations = geoLocations;
	}

	public ClientInfoDto getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInfoDto clientInfo) {
		this.clientInfo = clientInfo;
	}

	public DeviceInfoDto getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfoDto deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public NetworkInfoDto getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(NetworkInfoDto networkInfo) {
		this.networkInfo = networkInfo;
	}
}
