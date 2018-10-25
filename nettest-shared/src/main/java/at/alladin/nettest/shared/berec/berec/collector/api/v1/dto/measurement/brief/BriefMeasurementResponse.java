package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief;

import java.util.Map;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ClientTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

/**
 * The BriefMeasurementResponse contains the most important values of a measurement.
 * It is used to show a preview (list) of measurements to the end user.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "The BriefMeasurementResponse contains the most important values of a measurement. It is used to show a preview (list) of measurements to the end user.")
@JsonClassDescription("The BriefMeasurementResponse contains the most important values of a measurement. It is used to show a preview (list) of measurements to the end user.")
public class BriefMeasurementResponse {

// Measurement

	/**
	 * The UUIDv4 identifier of the measurement object.
	 */
	@io.swagger.annotations.ApiModelProperty("The UUIDv4 identifier of the measurement object.")
	@JsonPropertyDescription("The UUIDv4 identifier of the measurement object.")
	@Expose
	@SerializedName("uuid")
	@JsonProperty("uuid")
	private String uuid;

	/**
	 * Overall start time in UTC.
	 */
	@io.swagger.annotations.ApiModelProperty("Overall start time in UTC.")
	@JsonPropertyDescription("Overall start time in UTC.")
	@Expose
	@SerializedName("start_time")
	@JsonProperty("start_time")
	private LocalDateTime startTime;

	/**
	 * Overall duration of all sub measurements.
	 */
	@io.swagger.annotations.ApiModelProperty("Overall duration of all sub measurements.")
	@JsonPropertyDescription("Overall duration of all sub measurements.")
	@Expose
	@SerializedName("duration_ns")
	@JsonProperty("duration_ns")
	private Long durationNs;

// GeoLocationInfo

	/**
	 * The first GeoLocation i.e. the location where the measurement was started.
	 */
	@io.swagger.annotations.ApiModelProperty("The first GeoLocation i.e. the location where the measurement was started.")
	@JsonPropertyDescription("The first GeoLocation i.e. the location where the measurement was started.")
	@Expose
	@SerializedName("first_geo_location")
	@JsonProperty("first_geo_location")
	private GeoLocationDto firstGeoLocation;

// ClientInfo

	/**
	 * @see ClientType
	 */
	@io.swagger.annotations.ApiModelProperty("The type of client.")
	@JsonPropertyDescription("The type of client.")
    @Expose
	@SerializedName("type")
    @JsonProperty("type")
    private ClientTypeDto type;

// DeviceInfo

    /**
     * @see BriefDeviceInfo
     */
	@io.swagger.annotations.ApiModelProperty("BriefDeviceInfo contains the most important values from DeviceInfo class.")
	@JsonPropertyDescription("BriefDeviceInfo contains the most important values from DeviceInfo class.")
    @Expose
	@SerializedName("device_info")
    @JsonProperty("device_info")
    private BriefDeviceInfo deviceInfo;

// NetworkInfo

    /**
     * Network type id (@see NetworkType).
     */
	@io.swagger.annotations.ApiModelProperty("Network type id.")
	@JsonPropertyDescription("Network type id.")
    @Expose
	@SerializedName("network_type_id")
    @JsonProperty("network_type_id")
    private Integer networkTypeId;

    /**
     * Network type name (@see NetworkType).
     */
	@io.swagger.annotations.ApiModelProperty("Network type name.")
	@JsonPropertyDescription("Network type name.")
    @Expose
	@SerializedName("network_type_name")
    @JsonProperty("network_type_name")
    private String networkTypeName;

// SubMeasurement

	/**
	 * Map that contains available information for each measurement type (Speed, QoS).
	 * If map misses speed then no speed measurement was done, likewise for QoS, ...
	 */
	@io.swagger.annotations.ApiModelProperty("Map that contains available information for each measurement type (Speed, QoS). If map misses speed then no speed measurement was done, likewise for QoS, ...")
	@JsonPropertyDescription("Map that contains available information for each measurement type (Speed, QoS). If map misses speed then no speed measurement was done, likewise for QoS, ...")
	@Expose
	@SerializedName("measurements")
	@JsonProperty("measurements")
	private Map<MeasurementType, BriefSubMeasurement> measurements;

////

	/**
	 * Convenience method to check if this BriefMeasurementResponse contains a speed measurement.
	 * @return true if there is a speed measurement, false otherwise.
	 */
	public boolean isSpeedMeasurementAvailable() {
		return measurements != null && measurements.containsKey(MeasurementType.SPEED);
	}

	/**
	 * Convenience method to check if this BriefMeasurementResponse contains a QoS measurement.
	 * @return true if there is a QoS measurement, false otherwise.
	 */
	public boolean isQoSMeasurementAvailable() {
		return measurements != null && measurements.containsKey(MeasurementType.QOS);
	}

////

	/**
	 * BriefDeviceInfo contains the most important values from DeviceInfo class.
	 *
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	@io.swagger.annotations.ApiModel(description = "BriefDeviceInfo contains the most important values from DeviceInfo class.")
	@JsonClassDescription("BriefDeviceInfo contains the most important values from DeviceInfo class.")
	public static class BriefDeviceInfo {

		/**
		 * Device code name.
		 */
		@io.swagger.annotations.ApiModelProperty("Device code name.")
		@JsonPropertyDescription("Device code name.")
		@Expose
		@SerializedName("device_code_name")
		@JsonProperty("device_code_name")
		private String deviceCodeName;

	    /**
	     * The device name that is commonly known to users (e.g. Google Pixel).
	     */
		@io.swagger.annotations.ApiModelProperty("The device name that is commonly known to users (e.g. Google Pixel).")
		@JsonPropertyDescription("The device name that is commonly known to users (e.g. Google Pixel).")
	    @Expose
	    @SerializedName("device_full_name")
	    @JsonProperty("device_full_name")
	    private String deviceFullName;

	    /**
	     * Device operating system name.
	     */
		@io.swagger.annotations.ApiModelProperty("Device operating system name.")
		@JsonPropertyDescription("Device operating system name.")
	    @Expose
	    @SerializedName("os_name")
	    @JsonProperty("os_name")
	    private String osName;

	    /**
	     * Device operating system version.
	     */
		@io.swagger.annotations.ApiModelProperty("Device operating system version.")
		@JsonPropertyDescription("Device operating system version.")
	    @Expose
	    @SerializedName("os_version")
	    @JsonProperty("os_version")
	    private String osVersion;

	    /**
	     * Average CPU usage during the measurement.
	     */
		@io.swagger.annotations.ApiModelProperty("Average CPU usage during the measurement.")
		@JsonPropertyDescription("Average CPU usage during the measurement.")
	    @Expose
	    @SerializedName("avg_cpu_usage")
	    @JsonProperty("avg_cpu_usage")
	    private Double averageCpuUsage;

	    /**
	     * Average Memory usage during the measurement.
	     */
		@io.swagger.annotations.ApiModelProperty("Average Memory usage during the measurement.")
		@JsonPropertyDescription("Average Memory usage during the measurement.")
	    @Expose
	    @SerializedName("avg_mem_usage")
	    @JsonProperty("avg_mem_usage")
	    private Double averageMemUsage;
	}
}
