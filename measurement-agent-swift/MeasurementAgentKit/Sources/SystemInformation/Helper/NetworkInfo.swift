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
import CoreTelephony
import SystemConfiguration.CaptiveNetwork

public class NetworkInfo {

    private static let cellularNetworkIdDictionary = [
        CTRadioAccessTechnologyGPRS: 1,
        CTRadioAccessTechnologyEdge: 2,
        CTRadioAccessTechnologyWCDMA: 3,
        CTRadioAccessTechnologyCDMA1x: 4,
        CTRadioAccessTechnologyCDMAEVDORev0: 5,
        CTRadioAccessTechnologyCDMAEVDORevA: 6,
        CTRadioAccessTechnologyHSDPA: 8,
        CTRadioAccessTechnologyHSUPA: 9,
        CTRadioAccessTechnologyCDMAEVDORevB: 12,
        CTRadioAccessTechnologyLTE: 13,
        CTRadioAccessTechnologyeHRPD: 14
    ]

    private static let cellularNetworkNameDictionary = [
        CTRadioAccessTechnologyGPRS: "GPRS (2G)",
        CTRadioAccessTechnologyEdge: "EDGE (2G)",
        CTRadioAccessTechnologyWCDMA: "UMTS (3G)",
        CTRadioAccessTechnologyCDMA1x: "CDMA (2G)",
        CTRadioAccessTechnologyCDMAEVDORev0: "EVDO0 (2G)",
        CTRadioAccessTechnologyCDMAEVDORevA: "EVDOA (2G)",
        CTRadioAccessTechnologyHSDPA: "HSDPA (3G)",
        CTRadioAccessTechnologyHSUPA: "HSUPA (3G)",
        CTRadioAccessTechnologyCDMAEVDORevB: "EVDOB (2G)",
        CTRadioAccessTechnologyLTE: "LTE (4G)",
        CTRadioAccessTechnologyeHRPD: "HRPD (2G)"
    ]

    public class func getWifiInfo() -> (String?, String?) {
        #if targetEnvironment(simulator)
        logger.debug("running in simulator -> not possible to read wifi information")
        return (nil, nil)
        #else
        guard let interfaceNames = CNCopySupportedInterfaces() as? [String] else {
            return (nil, nil)
        }

        for name in interfaceNames {
            guard let interface = CNCopyCurrentNetworkInfo(name as CFString) as? [String: AnyObject] else {
                continue
            }

            guard let ssid = interface[kCNNetworkInfoKeySSID as String] as? String,
                let bssid = interface[kCNNetworkInfoKeyBSSID as String] as? String else {
                    continue
            }

            // TODO: support multiple ssid/bssid?
            return (ssid, bssid)
        }

        return (nil, nil)
        #endif
    }

    public class func getCellularNetworkType(_ str: String) -> Int? {
        return NetworkInfo.cellularNetworkIdDictionary[str]
    }

    public class func getCellularNetworkTypeDisplayName(_ str: String) -> String? {
        return NetworkInfo.cellularNetworkNameDictionary[str]
    }
}
