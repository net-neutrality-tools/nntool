package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result;

import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This DTO contains all relevant network information of a single point in time.
 *
 * @author fk@alladin.at
 *
 */
@io.swagger.annotations.ApiModel(description = "This DTO contains all relevant network information of a single point in time.")
@JsonClassDescription("This DTO contains all relevant network information of a single point in time.")
public class MeasurementResultNetworkPointInTime {

	/**
	 * Time and date the signal information was captured (UTC).
	 */
	@io.swagger.annotations.ApiModelProperty("Time and date the signal information was captured (UTC).")
	@JsonPropertyDescription("Time and date the signal information was captured (UTC).")
	@Expose
	@SerializedName("time")
	@JsonProperty("time")
	private LocalDateTime time;

	/**
     * Relative time in nanoseconds (to test begin).
	 */
	@io.swagger.annotations.ApiModelProperty("Time and date the signal information was captured (UTC).")
	@JsonPropertyDescription("Relative time in nanoseconds (to test begin).")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;

	/**
	 * Network type id as it gets returned by the Android API.
	 */
	@io.swagger.annotations.ApiModelProperty("Network type id as it gets returned by the Android API.")
	@JsonPropertyDescription("Network type id as it gets returned by the Android API.")
	@Expose
	@SerializedName("network_type_id")
	@JsonProperty("network_type_id")
	private Integer networkTypeId;

// _ ProviderInfo

// _ NetworkWifiInfo

	/**
     * SSID of the network.
     */
	@io.swagger.annotations.ApiModelProperty("SSID of the network.")
	@JsonPropertyDescription("SSID of the network.")
	@Expose
    @SerializedName("ssid")
	@JsonProperty("ssid")
    private String ssid;

    /**
     * BSSID of the network.
     */
	@io.swagger.annotations.ApiModelProperty("BSSID of the network.")
	@JsonPropertyDescription("BSSID of the network.")
	@Expose
    @SerializedName("bssid")
	@JsonProperty("bssid")
    private String bssid;

// _ NetworkMobileInfo

	/**
     * The network operator country code (e.g. "AT"), if available.
     */
	@io.swagger.annotations.ApiModelProperty("The network operator country code (e.g. \"AT\"), if available.")
	@JsonPropertyDescription("The network operator country code (e.g. \"AT\"), if available.")
    @Expose
	@SerializedName("network_country")
    @JsonProperty("network_country")
    private String networkCountry;

    /**
     * The MCC/MNC of the network operator, if available.
     */
	@io.swagger.annotations.ApiModelProperty("The MCC/MNC of the network operator, if available.")
	@JsonPropertyDescription("The MCC/MNC of the network operator, if available.")
    @Expose
    @SerializedName("network_operator_mcc_mnc")
    @JsonProperty("network_operator_mcc_mnc")
    private String networkOperatorMccMnc;

    /**
     * The network operator name, if available.
     */
	@io.swagger.annotations.ApiModelProperty("The network operator name, if available.")
	@JsonPropertyDescription("The network operator name, if available.")
    @Expose
    @SerializedName("network_operator_name")
    @JsonProperty("network_operator_name")
    private String networkOperatorName;

    /**
     * The SIM operator country code (e.g. "AT"), if available.
     */
	@io.swagger.annotations.ApiModelProperty("The SIM operator country code (e.g. \"AT\"), if available.")
	@JsonPropertyDescription("The SIM operator country code (e.g. \"AT\"), if available.")
    @Expose
    @SerializedName("sim_country")
    @JsonProperty("sim_country")
    private String simCountry;

    /**
     * The MCC/MNC of the SIM operator, if available.
     */
	@io.swagger.annotations.ApiModelProperty("The MCC/MNC of the SIM operator, if available.")
	@JsonPropertyDescription("The MCC/MNC of the SIM operator, if available.")
    @Expose
    @SerializedName("sim_operator_mcc_mnc")
    @JsonProperty("sim_operator_mcc_mnc")
    private String simOperatorMccMnc;

    /**
     * SIM operator name, if available.
     */
	@io.swagger.annotations.ApiModelProperty("SIM operator name, if available.")
	@JsonPropertyDescription("SIM operator name, if available.")
    @Expose
    @SerializedName("sim_operator_name")
    @JsonProperty("sim_operator_name")
    private String simOperatorName;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Long getRelativeTimeNs() {
		return relativeTimeNs;
	}

	public void setRelativeTimeNs(Long relativeTimeNs) {
		this.relativeTimeNs = relativeTimeNs;
	}

	public Integer getNetworkTypeId() {
		return networkTypeId;
	}

	public void setNetworkTypeId(Integer networkTypeId) {
		this.networkTypeId = networkTypeId;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getNetworkCountry() {
		return networkCountry;
	}

	public void setNetworkCountry(String networkCountry) {
		this.networkCountry = networkCountry;
	}

	public String getNetworkOperatorMccMnc() {
		return networkOperatorMccMnc;
	}

	public void setNetworkOperatorMccMnc(String networkOperatorMccMnc) {
		this.networkOperatorMccMnc = networkOperatorMccMnc;
	}

	public String getNetworkOperatorName() {
		return networkOperatorName;
	}

	public void setNetworkOperatorName(String networkOperatorName) {
		this.networkOperatorName = networkOperatorName;
	}

	public String getSimCountry() {
		return simCountry;
	}

	public void setSimCountry(String simCountry) {
		this.simCountry = simCountry;
	}

	public String getSimOperatorMccMnc() {
		return simOperatorMccMnc;
	}

	public void setSimOperatorMccMnc(String simOperatorMccMnc) {
		this.simOperatorMccMnc = simOperatorMccMnc;
	}

	public String getSimOperatorName() {
		return simOperatorName;
	}

	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}
}
