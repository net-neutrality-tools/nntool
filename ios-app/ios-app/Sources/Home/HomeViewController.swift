// ios-app: HomeViewController.swift, created on 19.03.19
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
import UIKit
import CoreTelephony
import Repeat
import MeasurementAgentKit
import Reachability

///
class HomeViewController: CustomNavigationBarViewController {

    @IBOutlet private var speedMeasurementGaugeView: SpeedMeasurementGaugeView?
    @IBOutlet private var deviceInfoView: HomeScreenDeviceInfoView?

    private let cpuUsageInfo = CpuUsageInfo()
    private let memoryUsageInfo = MemoryUsageInfo()
    private let locationTracker = LocationTracker()

    private var ipInfo: IPConnectivityInfo?
    private var reachability: Reachability?

    private var timer: Repeater?
    private var ipTimer: Repeater?

    private var previousInterfaceTraffic: InterfaceTraffic?

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        deviceInfoView?.reset()

        if MEASUREMENT_TRAFFIC_WARNING_ENABLED {
            speedMeasurementGaugeView?.startButtonActionCallback = displayPreMeasurementWarningAlert
        } else {
            speedMeasurementGaugeView?.startButtonActionCallback = {
                self.performSegue(withIdentifier: R.segue.homeViewController.show_speed_measurement_view_controller, sender: self)
            }
        }
    }

    ///
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        guard MEASUREMENT_AGENT.isRegistered() else {
            performSegue(withIdentifier: R.segue.homeViewController.present_modally_terms_and_conditions, sender: self)
            return
        }

        // TODO: display error message if location permission is not granted
        locationTracker.start(updateLocationCallback: { location in
            DispatchQueue.main.async {
                self.deviceInfoView?.locationLabel?.text = location.coordinate.dmFormattedString
                self.deviceInfoView?.locationInfoLabel?.text = "Location ±\(String(format: "%dm", Int(location.horizontalAccuracy)))"
            }
        })

        ipInfo = MEASUREMENT_AGENT.newIPConnectivityInfo()

        updateIpInfo()

        reachability = try? Reachability()
        reachability?.whenReachable = { r in
            self.updateIpInfo()

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

            DispatchQueue.main.async {
                self.speedMeasurementGaugeView?.isStartButtonEnabled = true

                self.speedMeasurementGaugeView?.networkTypeLabel?.text = networkTypeString
                self.speedMeasurementGaugeView?.networkDetailLabel?.text = networkDetailString
            }
        }
        reachability?.whenUnreachable = { r in
            self.updateIpInfo()

            DispatchQueue.main.async {
                self.speedMeasurementGaugeView?.isStartButtonEnabled = false

                self.speedMeasurementGaugeView?.networkTypeLabel?.text = "Unknown"
                self.speedMeasurementGaugeView?.networkDetailLabel?.text = "No connection"
            }
        }

        try? reachability?.startNotifier()

        ipTimer = Repeater.every(.seconds(30)) { _ in
            self.updateIpInfo()
        }

        updateDeviceInfo()

        timer = Repeater.every(.seconds(1)) { _ in
            self.updateDeviceInfo()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        locationTracker.stop()

        timer?.removeAllObservers(thenStop: true)
        timer = nil

        reachability?.stopNotifier()
        reachability = nil

        ipTimer?.removeAllObservers(thenStop: true)
        ipTimer = nil

        ipInfo = nil
    }

    func displayPreMeasurementWarningAlert() {
        // TODO: add localization
        let alert = UIAlertController(title: "Pre Measurement Alert", message: "Traffic Warning", preferredStyle: .alert)

        alert.addAction(UIAlertAction(title: "Abort", style: .default))

        alert.addAction(UIAlertAction(title: "Continue", style: .destructive) { _ in
            self.performSegue(withIdentifier: R.segue.homeViewController.show_speed_measurement_view_controller, sender: self)
        })

        present(alert, animated: true, completion: nil)
    }

    private func updateDeviceInfo() {
        DispatchQueue.global(qos: .background).async {
            if let cpuUsage = self.cpuUsageInfo.getCurrentCpuUsagePercent() {
                DispatchQueue.main.async {
                    self.deviceInfoView?.cpuValueLabel?.text = String(format: "%0.2f%%", cpuUsage * 100)
                }
            } else {
                DispatchQueue.main.async {
                    self.deviceInfoView?.cpuValueLabel?.text = "n/a%"
                }
            }

            if let memoryUsage = self.memoryUsageInfo.getCurrentMemoryUsagePercent() {
                DispatchQueue.main.async {
                    self.deviceInfoView?.memValueLabel?.text = String(format: "%0.2f%%", memoryUsage * 100)
                }
            } else {
                DispatchQueue.main.async {
                    self.deviceInfoView?.cpuValueLabel?.text = "n/a%"
                }
            }

            if let traffic = InterfaceTrafficInfo.getWwanAndWifiNetworkInterfaceTraffic() {
                if let previousTraffic = self.previousInterfaceTraffic {
                    let diff = traffic.differenceTo(previousTraffic)
                    let (txClassification, rxClassification) = diff.classify()

                    let txStr = AttributedStringHelper.repeatedColoredIconString(.trafficOut, count: 3, activeCount: txClassification.rawValue, ltr: true)
                    let rxStr = AttributedStringHelper.repeatedColoredIconString(.trafficIn, count: 3, activeCount: rxClassification.rawValue, ltr: false)

                    DispatchQueue.main.async {
                        self.deviceInfoView?.trafficOutValueLabel?.attributedText = txStr
                        self.deviceInfoView?.trafficInValueLabel?.attributedText = rxStr
                    }
                }

                self.previousInterfaceTraffic = traffic
            }
        }
    }

    private func updateIpInfo() {
        DispatchQueue.global(qos: .background).async {
            self.updateIpStatus(self.ipInfo?.checkIPv4Connectivity(), label: self.deviceInfoView?.ipv4ValueLabel)
        }

        DispatchQueue.global(qos: .background).async {
            self.updateIpStatus(self.ipInfo?.checkIPv6Connectivity(), label: self.deviceInfoView?.ipv6ValueLabel)
        }
    }

    private func updateIpStatus(_ status: IPStatus?, label: UILabel?) {
        var icon: IconFont = .cross
        var color = BEREC_DARK_GRAY

        if let status = status, status.hasInternetConnection {
            icon = .check
            if status.isNat {
                color = UIColor.yellow
            } else {
                color = UIColor.green
            }
        }

        DispatchQueue.main.async {
            label?.icon = icon
            label?.textColor = color
        }
    }
}
