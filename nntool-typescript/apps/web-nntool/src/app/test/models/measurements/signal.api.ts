export class SignalAPI {

    /**
     * Network type id as it gets returned by the Android API.
     */
    network_type_id: number;

    /**
     * Time and date the signal information was captured (UTC).
     */
    time: string; // TODO: change back to Date

    /**
     * Relative time in nanoseconds (to test begin).
     */
    relative_time_ns: number;

    /**
     * The current WiFi link speed, in bits per second (If available).
     */
    wifi_link_speed_bps: number;

    /**
     * The received signal strength indicator of the current 802.11 network, in dBm (If available).
     */
    wifi_rssi_dbm: number;

    /**
     * The received signal strength of 2G or 3G connections, in dBm (If available).
     */
    signal_strength_2g3g_dbm: number;

// 	/**
// 	 * The bit error rate as defined in (ETSI) TS 27.007 8.5 (If available).
// 	 */
// 	@io.swagger.annotations.ApiModelProperty("The bit error rate as defined in (ETSI) TS 27.007 8.5 (If available).")
// 	@JsonPropertyDescription("The bit error rate as defined in (ETSI) TS 27.007 8.5 (If available).")
// 	@Expose
// 	@SerializedName("gsm_bit_error_rate")
// 	@JsonProperty("gsm_bit_error_rate")
// 	private Integer gsmBitErrorRate;

    /**
     * The LTE reference signal received power, in dBm (If available).
     */
    lte_rsrp_dbm: number;

    /**
     * The LTE reference signal received quality, in dB (If available).
     */
    lte_rsrp_db: number;

    /**
     * The LTE reference signal signal-to-noise ratio, in dB (If available).
     */
    lte_rssnr_db: number;

    /**
     * The LTE channel quality indicator (If available).
     */
    lte_cqi: number;

    /**
     * SSID of the network.
     */
    wifi_ssid: string;

    /**
     * BSSID of the network.
     */
    wifi_bssid: string;

}

