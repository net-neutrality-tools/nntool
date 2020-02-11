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
import MeasurementAgentKit
import CoreTelephony

class NetworkInfoReachability {

    private var reachability: Reachability?
    // swiftlint:disable:next cyclomatic_complexity function_body_length
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
                networkTypeString = ssid ?? R.string.localizable.networkWifiUnknown()
                #endif

                networkDetailString = R.string.localizable.networkWifiTechnologyName()
            case .cellular:
                var carrierData = [String: AnyObject]()
                var simsActive = 0

                var pSimCarrierName: String?
                var pSimMobileCountryCode: String?
                var pSimMobileNetworkCode: String?
                var pSimCurrentRadioAccessTechnology: String?

                let telephonyNetworkInfo = CTTelephonyNetworkInfo()

                //physical sim
                let carrierInfo = telephonyNetworkInfo.subscriberCellularProvider!

                pSimCarrierName = carrierInfo.carrierName
                pSimMobileCountryCode = carrierInfo.mobileCountryCode
                pSimMobileNetworkCode = carrierInfo.mobileNetworkCode
                pSimCurrentRadioAccessTechnology = telephonyNetworkInfo.currentRadioAccessTechnology

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

                        aSimCarrierName = carrierInfo.carrierName
                        aSimMobileCountryCode = carrierInfo.mobileCountryCode
                        aSimMobileNetworkCode = carrierInfo.mobileNetworkCode

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
                            if networkTypeString == nil {
                                networkTypeString = ""
                            } else {
                                networkTypeString! += " | "
                            }
                            if networkDetailString == nil {
                                networkDetailString = ""
                            } else {
                                networkDetailString! += " | "
                            }

                            networkTypeString! += (carrierDict["carrier"] as? String)!
                            networkDetailString! += (carrierDict["mcc"] as? String)! + "-" + (carrierDict["mnc"] as? String)!
                            if carrierDict["technology"] as? String != nil {
                                currentRadioAccessTechnology = (carrierDict["technology"] as? String)!
                            }
                        }
                    }
                }

                let cellularNetworkDisplayName = NetworkInfo.getCellularNetworkTypeDisplayName(currentRadioAccessTechnology!)
                if currentRadioAccessTechnology != nil && cellularNetworkDisplayName != nil {
                    networkDetailString = cellularNetworkDisplayName! + ", " + networkDetailString!
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
