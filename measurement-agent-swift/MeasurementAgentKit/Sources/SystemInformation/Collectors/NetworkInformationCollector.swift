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

    private let connectivityService: IPConnectivityInfo?

    private var reachability: Reachability?

    private var lastReportedNetworkInfo: MeasurementResultNetworkPointInTimeDto?

    init(connectivityService: IPConnectivityInfo?) {
        self.connectivityService = connectivityService

        super.init()
    }

    /// network info collector should only collect if there's an update and not be triggered by 1 second interval!
    override func start(_ timeBasedResult: TimeBasedResultDto, startNs: UInt64) {
        super.start(timeBasedResult, startNs: startNs)

        reachability = try? Reachability()

        reachability?.whenReachable = { r in
            let networkPointInTime = MeasurementResultNetworkPointInTimeDto()

            switch r.connection {
            case .wifi:
                self.fillWifiInformation(dto: networkPointInTime)
            case .cellular:
                self.fillCellularInformation(dto: networkPointInTime)
            default:
                // no connection?
                logger.debug("NO CONN!!! \(r.connection)")
                break
            }

            networkPointInTime.time = Date()
            networkPointInTime.relativeTimeNs = self.currentRelativeTimeNs()

            if let conn = self.connectivityService {
                // TODO: send both ipv4 and ipv6 ip addresses or request only information for current active family?

                // Delay IP address request a few seconds to not interfere with the NativeScript intialization phase.
                // If we don't wait some seconds the IP request would time out (main thread + Siesta).
                // It may work if we start the request on a background thread, but then we would have to get rid of Siesta.
                DispatchQueue.global(qos: .background).asyncAfter(deadline: .now() + .seconds(5)) {
                    let ipv4Connectivity = conn.checkIPv4Connectivity()

                    if ipv4Connectivity?.hasInternetConnection ?? false {
                        networkPointInTime.agentPublicIp = ipv4Connectivity?.publicAddress
                        networkPointInTime.agentPrivateIp = ipv4Connectivity?.localAddress
                    } else {
                        let ipv6Connectivity = conn.checkIPv6Connectivity()

                        if ipv6Connectivity?.hasInternetConnection ?? false {
                            networkPointInTime.agentPublicIp = ipv6Connectivity?.publicAddress
                            networkPointInTime.agentPrivateIp = ipv6Connectivity?.localAddress
                        }
                    }
                }
            }

            logger.debug("NETWORK: \(networkPointInTime.debugDescription)")

            timeBasedResult.networkPointsInTime?.append(networkPointInTime)
        }
        reachability?.whenUnreachable = { r in
            let networkPointInTime = MeasurementResultNetworkPointInTimeDto()

            // TODO
            //networkPointInTime.networkTypeId = -1

            timeBasedResult.networkPointsInTime?.append(networkPointInTime)
        }

        try? reachability?.startNotifier()
    }

    override func stop() {
        super.stop()

        reachability?.stopNotifier()
        reachability = nil
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
}
