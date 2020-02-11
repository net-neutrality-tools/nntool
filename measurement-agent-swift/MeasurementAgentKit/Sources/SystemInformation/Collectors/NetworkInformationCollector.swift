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
        var carrierData = [String: AnyObject]()
        var simsActive = 0

        var pSimCarrierName: String?
        var pSimMobileCountryCode: String?
        var pSimMobileNetworkCode: String?
        var pSimCurrentRadioAccessTechnology: String?
        var pSimIsoCountryCode: String?

        let telephonyNetworkInfo = CTTelephonyNetworkInfo()

        //physical sim
        let carrierInfo = telephonyNetworkInfo.subscriberCellularProvider!

        pSimCarrierName = carrierInfo.carrierName
        pSimMobileCountryCode = carrierInfo.mobileCountryCode
        pSimMobileNetworkCode = carrierInfo.mobileNetworkCode
        pSimCurrentRadioAccessTechnology = telephonyNetworkInfo.currentRadioAccessTechnology
        pSimIsoCountryCode = carrierInfo.isoCountryCode

        var pSim = [String: AnyObject]()

        if pSimCarrierName == nil || pSimMobileCountryCode == nil || pSimMobileNetworkCode == nil
        {
            pSim["active"] = false as AnyObject
        }
        if pSimCarrierName != nil {
            pSim["carrier"] = pSimCarrierName as AnyObject
        }
        if pSimMobileCountryCode != nil {
            pSim["mcc"] = pSimMobileCountryCode as AnyObject
            pSim["active"] = true as AnyObject
            simsActive+=1
        }
        if pSimMobileNetworkCode != nil {
            pSim["mnc"] = pSimMobileNetworkCode as AnyObject
        }
        if pSimCurrentRadioAccessTechnology != nil {
            pSim["technology"] = pSimCurrentRadioAccessTechnology as AnyObject
        }
        if pSimIsoCountryCode != nil {
            pSim["iso_country_code"] = pSimIsoCountryCode as AnyObject
        }

        carrierData["sim_physical"] = pSim as AnyObject

        //service subscriber cellular providers
        let aSimArray = NSMutableArray()
        if #available(iOS 12.0, *) {
            if telephonyNetworkInfo.serviceSubscriberCellularProviders!.count > 0 {
                simsActive = 0
            } else {
                aSimArray.add(pSim)
            }

            for (key, carrierInfo) in telephonyNetworkInfo.serviceSubscriberCellularProviders! {
                var aSimCarrierName: String?
                var aSimMobileCountryCode: String?
                var aSimMobileNetworkCode: String?
                var aSimIsoCountryCode: String?

                aSimCarrierName = carrierInfo.carrierName
                aSimMobileCountryCode = carrierInfo.mobileCountryCode
                aSimMobileNetworkCode = carrierInfo.mobileNetworkCode
                aSimIsoCountryCode = carrierInfo.isoCountryCode

                var aSim = [String: AnyObject]()

                if aSimCarrierName == nil || aSimMobileCountryCode == nil || aSimMobileNetworkCode == nil {
                    aSim["active"] = false as AnyObject
                }
                if aSimCarrierName != nil {
                    aSim["carrier"] = aSimCarrierName as AnyObject
                }
                if aSimMobileCountryCode != nil {
                    aSim["mcc"] = aSimMobileCountryCode as AnyObject
                    aSim["active"] = true as AnyObject
                    simsActive+=1
                }
                if aSimMobileNetworkCode != nil {
                    aSim["mnc"] = aSimMobileNetworkCode as AnyObject
                }
                if telephonyNetworkInfo.serviceCurrentRadioAccessTechnology![key] != nil {
                    aSim["technology"] = telephonyNetworkInfo.serviceCurrentRadioAccessTechnology![key] as AnyObject
                }
                if aSimIsoCountryCode != nil {
                    aSim["iso_country_code"] = aSimIsoCountryCode as AnyObject
                }

                aSimArray.add(aSim)
            }
        } else {
            aSimArray.add(pSim)
        }

        if simsActive == 0 {
            NSLog("No (e)SIM Card active or no (e)SIM Card Slot present")
        } else if simsActive > 1 {
            NSLog("Multiple (e)SIM Cards active")
        }

        carrierData["sims_active"] = simsActive as AnyObject
        carrierData["sims_available"] = aSimArray as AnyObject

        var currentRadioAccessTechnology: String?

        for carrier in aSimArray {
            if let carrierDict = carrier as? [String: AnyObject] {
                if carrierDict["active"] as? integer_t == 1 {
                    if carrierDict["iso_country_code"] != nil {
                        dto.simCountry = (carrierDict["iso_country_code"] as? String)!
                    }
                    if carrierDict["mcc"] != nil && carrierDict["mnc"] != nil {
                        dto.simOperatorMccMnc = (carrierDict["mcc"] as? String)! + "-" + (carrierDict["mnc"] as? String)!
                    }
                    if carrierDict["carrier"] != nil {
                        dto.simOperatorName = (carrierDict["carrier"] as? String)!
                    }
                    if carrierDict["technology"] != nil {
                        dto.networkTypeId = NetworkInfo.getCellularNetworkType((carrierDict["technology"] as? String)!)
                    }
                    
                }
            }
        }
    }
}
