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

import Foundation

/// Contains signal information from a point in time on the measurement agent.
class SignalDto: Codable {

    /// Network type id as it gets returned by the Android API.
    var networkTypeId: Int?

    /// Time and date the signal information was captured (UTC).
    var time: Date?

    /// Relative time in nanoseconds (to test begin).
    var relativeTimeNs: UInt64?

    /// The current WiFi link speed, in bits per second (If available).
    var wifiLinkSpeedBps: Int?

    /// The received signal strength indicator of the current 802.11 network, in dBm (If available).
    var wifiRssiDbm: Int?

    /// The received signal strength of 2G or 3G connections, in dBm (If available).
    var signalStrength2g3gDbm: Int?

    /// The LTE reference signal received power, in dBm (If available).
    var lteRsrpDbm: Int?

    /// The LTE reference signal received quality, in dB (If available).
    var lteRsrqDb: Int?

    /// The LTE reference signal signal-to-noise ratio, in dB (If available).
    var lteRssnrDb: Int?

    /// The LTE channel quality indicator (If available).
    var lteCqi: Int?

    /// SSID of the network.
    var wifiSsid: String?

    /// BSSID of the network.
    var wifiBss: String?

    ///
    enum CodingKeys: String, CodingKey {
        case networkTypeId         = "network_type_id"
        case time
        case relativeTimeNs        = "relative_time_ns"
        case wifiLinkSpeedBps      = "wifi_link_speed_bps"
        case wifiRssiDbm           = "wifi_rssi_dbm"
        case signalStrength2g3gDbm = "signal_strength_2g3g_dbm"
        case lteRsrpDbm            = "lte_rsrp_dbm"
        case lteRsrqDb             = "lte_rsrp_db"
        case lteRssnrDb            = "lte_rssnr_db"
        case lteCqi                = "lte_cqi"
        case wifiSsid              = "wifi_ssid"
        case wifiBss               = "wifi_bssid"
    }
}
