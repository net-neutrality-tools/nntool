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

export class SignalAPI {
  /**
   * Network type id as it gets returned by the Android API.
   */
  public network_type_id: number;

  /**
   * Time and date the signal information was captured (UTC).
   */
  public time: string; // TODO: change back to Date

  /**
   * Relative time in nanoseconds (to test begin).
   */
  public relative_time_ns: number;

  /**
   * The current WiFi link speed, in bits per second (If available).
   */
  public wifi_link_speed_bps: number;

  /**
   * The received signal strength indicator of the current 802.11 network, in dBm (If available).
   */
  public wifi_rssi_dbm: number;

  /**
   * The received signal strength of 2G or 3G connections, in dBm (If available).
   */
  public signal_strength_2g3g_dbm: number;

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
  public lte_rsrp_dbm: number;

  /**
   * The LTE reference signal received quality, in dB (If available).
   */
  public lte_rsrp_db: number;

  /**
   * The LTE reference signal signal-to-noise ratio, in dB (If available).
   */
  public lte_rssnr_db: number;

  /**
   * The LTE channel quality indicator (If available).
   */
  public lte_cqi: number;

  /**
   * SSID of the network.
   */
  public wifi_ssid: string;

  /**
   * BSSID of the network.
   */
  public wifi_bssid: string;
}
