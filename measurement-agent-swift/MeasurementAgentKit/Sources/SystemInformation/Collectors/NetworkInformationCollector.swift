// MeasurementAgentKit: NetworkInformationCollector.swift, created on 01.07.19
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
import CoreTelephony

class NetworkInformationCollector: BaseInformationCollector {

    private var reachability: Reachability?

    override func start(_ timeBasedResult: TimeBasedResultDto, startNs: UInt64) {
        timeBasedResult.networkPointsInTime = [MeasurementResultNetworkPointInTimeDto]()

        reachability = try? Reachability()
        //try? reachability?.startNotifier()
    }

    override func collect(into timeBasedResult: TimeBasedResultDto) {
        guard let r = reachability else {
            return
        }

        let networkPointInTime = MeasurementResultNetworkPointInTimeDto()

        switch r.connection {
        case .wifi:
            fillWifiInformation(dto: networkPointInTime)
        case .cellular:
            fillCellularInformation(dto: networkPointInTime)
        default:
            // no connection?
            logger.debug("NO CONN!!! \(r.connection)")
            break
        }

        networkPointInTime.time = Date()
        networkPointInTime.relativeTimeNs = currentRelativeTimeNs()

        logger.debug("NETWORK: \(networkPointInTime.debugDescription)")

        timeBasedResult.networkPointsInTime?.append(networkPointInTime)
    }

    private func fillWifiInformation(dto: MeasurementResultNetworkPointInTimeDto) {
        let (ssid, bssid) = NetworkInfo.getWifiInfo()

        dto.ssid = ssid
        dto.bssid = bssid

        dto.networkTypeId = 99
    }

    private func fillCellularInformation(dto: MeasurementResultNetworkPointInTimeDto ) {
        //networkPointInTime.networkCountry
        //networkPointInTime.networkOperatorName
        //networkPointInTime.networkOperatorMccMnc

        let telephonyNetworkInfo = CTTelephonyNetworkInfo()

        let carrier = telephonyNetworkInfo.subscriberCellularProvider

        dto.simCountry = carrier?.isoCountryCode

        if let mcc = carrier?.mobileCountryCode, let mnc = carrier?.mobileNetworkCode {
            dto.simOperatorMccMnc = mcc + "-" + mnc
        }

        dto.simOperatorName = carrier?.carrierName

        if let currentRadioAccessTechnology = telephonyNetworkInfo.currentRadioAccessTechnology {
            dto.networkTypeId = NetworkInfo.getCellularNetworkType(currentRadioAccessTechnology)
        }

        // TODO: multi-sim support?
    }

    deinit {

    }
}
