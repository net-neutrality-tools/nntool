package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.BasicRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.CellLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.PointInTimeValueDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;

/**
 * This DTO contains the full measurement data that is sent from the client to the server.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO contains the full measurement data that is sent from the client to the server.")
@JsonClassDescription("This DTO contains the full measurement data that is sent from the client to the server.")
public class MeasurementResultRequest extends BasicRequest {

	/**
	 * Contains the result of a Speed and/or QoS measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("Contains the result of a Speed and/or QoS measurement.")
	@JsonPropertyDescription("Contains the result of a Speed and/or QoS measurement.")
	@Expose
	@SerializedName("measurements")
	@JsonProperty("measurements")
	private /*Enum*/Map<MeasurementType, SubMeasurementResult> measurements;
	
// MeasurementTime
		
	/**
	 * Start date and time for this sub measurement. Date and time is always stored as UTC.
	 */
	@io.swagger.annotations.ApiModelProperty("Start date and time for this sub measurement. Date and time is always stored as UTC.")
	@JsonPropertyDescription("Start date and time for this sub measurement. Date and time is always stored as UTC.")
	@Expose
	@SerializedName("start_time")
	@JsonProperty("start_time")
	private LocalDateTime startTime;
	
	/**
	 * End date and time for this sub measurement. Date and time is always stored as UTC.
	 */
	@io.swagger.annotations.ApiModelProperty("End date and time for this sub measurement. Date and time is always stored as UTC.")
	@JsonPropertyDescription("End date and time for this sub measurement. Date and time is always stored as UTC.")
	@Expose
	@SerializedName("end_time")
	@JsonProperty("end_time")
	private LocalDateTime endTime;
	
	/**
	 * Overall duration of this measurement.
	 */
	@io.swagger.annotations.ApiModelProperty("Overall duration of this measurement.")
	@JsonPropertyDescription("Overall duration of this measurement.")
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
	
	// -> everything already submitted by ApiRequestInfo
	
// DeviceInfo
	
	// -> everything already submitted by ApiRequestInfo
	
// _ OperatingSystemInfo
		
	/**
	 * CPU usage during the test, if available.
	 */
	@io.swagger.annotations.ApiModelProperty("CPU usage during the test, if available.")
	@JsonPropertyDescription("CPU usage during the test, if available.")
	@Expose
	@SerializedName("cpu_usage")
	@JsonProperty("cpu_usage")
	private List<PointInTimeValueDto<Double>> cpuUsage;
	
	/**
	 * Memory usage during the test, if available.
	 */
	@io.swagger.annotations.ApiModelProperty("Memory usage during the test, if available.")
	@JsonPropertyDescription("Memory usage during the test, if available.")
	@Expose
	@SerializedName("mem_usage")
	@JsonProperty("mem_usage")
	private List<PointInTimeValueDto<Double>> memUsage;
	
// NetworkInfo
	
// _ EmbeddedNetworkType

	/**
	 * Contains all relevant network information of a single point in time.
	 * @see NetworkPointInTime
	 */
	@io.swagger.annotations.ApiModelProperty("Contains all relevant network information of a single point in time.")
	@JsonPropertyDescription("Contains all relevant network information of a single point in time.")
	@Expose
	@SerializedName("network_points_in_time")
	@JsonProperty("network_points_in_time")
	private List<MeasurementResultNetworkPointInTime> networkPointsInTime;
	
// _ CellLocationInfo
	
	/**
	 * List of captured cell information.
	 */
	@io.swagger.annotations.ApiModelProperty("List of captured cell information.")
	@JsonPropertyDescription("List of captured cell information.")
	@Expose
	@SerializedName("cell_locations")
	@JsonProperty("cell_locations")
	private List<CellLocationDto> cellLocations;
	
// _ SignalInfo

	/**
	 * List of captured signal information.
	 */
	@io.swagger.annotations.ApiModelProperty("List of captured signal information.")
	@JsonPropertyDescription("List of captured signal information.")
	@Expose
	@SerializedName("signals")
	@JsonProperty("signals")
	private List<SignalDto> signals;
	
	// We don't need to submit the following values because they are either generated by the server or 
	// already submitted in the initiation request or via ApiRequestInfo:
	// - uuid
	// - openDataUuid
	// - source
	// ... fields from NetworkInfo, etc.

	public Map<MeasurementType, SubMeasurementResult> getMeasurements() {
		return measurements;
	}

	public void setMeasurements(Map<MeasurementType, SubMeasurementResult> measurements) {
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

	public List<PointInTimeValueDto<Double>> getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(List<PointInTimeValueDto<Double>> cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public List<PointInTimeValueDto<Double>> getMemUsage() {
		return memUsage;
	}

	public void setMemUsage(List<PointInTimeValueDto<Double>> memUsage) {
		this.memUsage = memUsage;
	}

	public List<MeasurementResultNetworkPointInTime> getNetworkPointsInTime() {
		return networkPointsInTime;
	}

	public void setNetworkPointsInTime(List<MeasurementResultNetworkPointInTime> networkPointsInTime) {
		this.networkPointsInTime = networkPointsInTime;
	}

	public List<CellLocationDto> getCellLocations() {
		return cellLocations;
	}

	public void setCellLocations(List<CellLocationDto> cellLocations) {
		this.cellLocations = cellLocations;
	}

	public List<SignalDto> getSignals() {
		return signals;
	}

	public void setSignals(List<SignalDto> signals) {
		this.signals = signals;
	}
}
