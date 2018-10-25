package at.alladin.nettest.shared.server.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains signal information from a point in time.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains signal information from a point in time.")
public class Signal {

	/**
	 * Network type id as it gets returned by the Android API.
	 */
	@JsonPropertyDescription("Network type id as it gets returned by the Android API.")
	@Expose
	@SerializedName("network_type_id")
	@JsonProperty("network_type_id")
	private Integer networkTypeId;
	
	/**
	 * Time and date the signal information was captured (UTC).
	 */
	@JsonPropertyDescription("Time and date the signal information was captured (UTC).")
	@Expose
	@SerializedName("time")
	@JsonProperty("time")
	private LocalDateTime time;
	
	/**
     * Relative time in nanoseconds (to test begin).
	 */
	@JsonPropertyDescription("Relative time in nanoseconds (to test begin).")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;

	/**
	 * The current WiFi link speed, in bits per second (If available).
	 */
	@JsonPropertyDescription("The current WiFi link speed, in bits per second (If available).")
	@Expose
	@SerializedName("wifi_link_speed_bps")
	@JsonProperty("wifi_link_speed_bps")	
	private Integer wifiLinkSpeedBps;
	
	/**
	 * The received signal strength indicator of the current 802.11 network, in dBm (If available).
	 */
	@JsonPropertyDescription("The received signal strength indicator of the current 802.11 network, in dBm (If available).")
	@Expose
	@SerializedName("wifi_rssi_dbm")
	@JsonProperty("wifi_rssi_dbm")
	private Integer wifiRssiDbm;
	
	/**
	 * The received signal strength of 2G or 3G connections, in dBm (If available).
	 */
	@JsonPropertyDescription("The received signal strength of 2G or 3G connections, in dBm (If available).")
	@Expose
	@SerializedName("signal_strength_2g3g_dbm")
	@JsonProperty("signal_strength_2g3g_dbm")	
	private Integer signalStrength2g3gDbm;
	
	/**
	 * The bit error rate as defined in (ETSI) TS 27.007 8.5 (If available).
	 */
	@JsonPropertyDescription("The bit error rate as defined in (ETSI) TS 27.007 8.5 (If available).")
	@Expose
	@SerializedName("gsm_bit_error_rate")
	@JsonProperty("gsm_bit_error_rate")	
	private Integer gsmBitErrorRate;
	
    /**
     * The LTE reference signal received power, in dBm (If available).
     */
	@JsonPropertyDescription("The LTE reference signal received power, in dBm (If available).")
    @Expose
    @SerializedName("lte_rsrp_dbm")
    @JsonProperty("lte_rsrp_dbm")
    private Integer lteRsrpDbm;
    
    /**
     * The LTE reference signal received quality, in dB (If available).
     */
	@JsonPropertyDescription("The LTE reference signal received quality, in dB (If available).")
    @Expose
    @SerializedName("lte_rsrp_db")
    @JsonProperty("lte_rsrp_db")
    private Integer lteRsrqDb;   
    
    /**
     * The LTE reference signal signal-to-noise ratio, in dB (If available).
     */
	@JsonPropertyDescription("The LTE reference signal signal-to-noise ratio, in dB (If available).")
    @Expose
    @SerializedName("lte_rssnr_db")
    @JsonProperty("lte_rssnr_db")
    private Integer lteRssnrDb;
    
    /**
     * The LTE channel quality indicator (If available).
     */
	@JsonPropertyDescription("The LTE channel quality indicator (If available).")
    @Expose
    @SerializedName("lte_cqi")
    @JsonProperty("lte_cqi")
    private Integer lteCqi;
	
    /**
     * SSID of the network (If available).
     */
	@JsonPropertyDescription("SSID of the network (If available).")
	@Expose
    @SerializedName("wifi_ssid")
	@JsonProperty("wifi_ssid")
    private String wifiSsid;
    
    /**
     * BSSID of the network (If available).
     */
	@JsonPropertyDescription("BSSID of the network (If available).")
	@Expose
    @SerializedName("wifi_bssid")
	@JsonProperty("wifi_bssid")
    private String wifiBssid;
}
