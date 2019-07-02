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
import SystemConfiguration.CaptiveNetwork

private let cellularNetworkIdDictionary = [
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
        #if targetEnvironment(simulator)
        logger.debug("running in simulator -> not possible to read wifi information")
        #else
        guard let interfaceNames = CNCopySupportedInterfaces() as? [String] else {
            return
        }
        
        for name in interfaceNames {
            guard let interface = CNCopyCurrentNetworkInfo(name as CFString) as? [String: AnyObject] else {
                continue
            }
            
            guard let ssid = interface[kCNNetworkInfoKeySSID as String] as? String,
                let bssid = interface[kCNNetworkInfoKeyBSSID as String] as? String else {
                    
                    continue
            }
            
            dto.ssid = ssid
            dto.bssid = bssid
            
            dto.networkTypeId = 99
            
            break
        }
        #endif

        // TODO: support multiple ssid/bssid?
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
            dto.networkTypeId = cellularNetworkIdDictionary[currentRadioAccessTechnology]
        }

        // TODO: multi-sim support?
    }

    deinit {

    }
}
