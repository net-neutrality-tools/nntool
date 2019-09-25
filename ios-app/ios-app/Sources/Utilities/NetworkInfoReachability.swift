// ios-app: NetworkInfoReachability.swift, created on 12.09.19
/*******************************************************************************
 * Copyright 2019 Benjamin Pucher (alladin-IT GmbH)
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
import Reachability
import MeasurementAgentKit
import CoreTelephony

class NetworkInfoReachability {

    private var reachability: Reachability?

    init(whenReachable: ((String?, String?) -> Void)?, whenUnreachable: (() -> Void)?) {
        reachability = try? Reachability()
        reachability?.whenReachable = { r in
            var networkTypeString: String?
            var networkDetailString: String?

            switch r.connection {
            case .wifi:
                let (ssid, _) = NetworkInfo.getWifiInfo()

                #if targetEnvironment(simulator)
                networkTypeString = "Simulator Network"
                #else
                networkTypeString = ssid ?? "Unknown"
                #endif

                networkDetailString = "WiFi"
            case .cellular:
                let telephonyNetworkInfo = CTTelephonyNetworkInfo()
                let carrier = telephonyNetworkInfo.subscriberCellularProvider

                networkTypeString = carrier?.carrierName

                if  let mcc = carrier?.mobileCountryCode,
                    let mnc = carrier?.mobileNetworkCode,
                    let currentRadioAccessTechnology = telephonyNetworkInfo.currentRadioAccessTechnology,
                    let cellularNetworkDisplayName = NetworkInfo.getCellularNetworkTypeDisplayName(currentRadioAccessTechnology) {

                    networkDetailString = "\(cellularNetworkDisplayName), \(mcc)-\(mnc)"
                }
            default:
                break
            }

            whenReachable?(networkTypeString, networkDetailString)
        }
        reachability?.whenUnreachable = { r in
            whenUnreachable?()
        }
    }

    func start() {
        try? reachability?.startNotifier()
    }

    func stop() {
        reachability?.stopNotifier()
    }
}
