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

    @IBOutlet private var selectSpeedMeasurementPeerLabel: UILabel?
    @IBOutlet private var selectSpeedMeasurementPeerSeparatorView: UIView?
    @IBOutlet private var selectSpeedMeasurementPeerContainerView: UIView?
    
    private var currentMeasurementPeerTableViewController: CurrentMeasurementPeerTableViewController?

    private let cpuUsageInfo = CpuUsageInfo()
    private let memoryUsageInfo = MemoryUsageInfo()
    private let locationTracker = LocationTracker()

    private var ipInfo: IPConnectivityInfo?
    private var reachability: NetworkInfoReachability?

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

        loadMeasurementPeers()
        start()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        stop()
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }

        switch identifier {
        case R.segue.homeViewController.present_modally_terms_and_conditions.identifier:
            ((segue.destination as? UINavigationController)?.viewControllers.first as? TermsAndConditionsViewController)?.delegate = self
        case R.segue.homeViewController.embed_current_measurement_peer.identifier:
            currentMeasurementPeerTableViewController = segue.destination as? CurrentMeasurementPeerTableViewController
        case R.segue.homeViewController.show_speed_measurement_view_controller.identifier:
            if let measurementViewController = segue.destination as? MeasurementViewController {
                measurementViewController.preferredSpeedMeasurementPeer = currentMeasurementPeerTableViewController?.selectedMeasurementPeer
            }
        default: break
        }
    }

    /// Load measurement peer list.
    private func loadMeasurementPeers() {
        if currentMeasurementPeerTableViewController?.measurementPeers == nil {
            MEASUREMENT_AGENT.getSpeedMeasurementPeers(onSuccess: { peers in
                logger.debug(peers)

                DispatchQueue.main.async {
                    self.currentMeasurementPeerTableViewController?.measurementPeers = peers

                    self.selectSpeedMeasurementPeerLabel?.isHidden = false
                    self.selectSpeedMeasurementPeerSeparatorView?.isHidden = false
                    self.selectSpeedMeasurementPeerContainerView?.isHidden = false
                }
            }, onFailure: { error in
                logger.debug(error)

                DispatchQueue.main.async {
                    self.selectSpeedMeasurementPeerLabel?.isHidden = true
                    self.selectSpeedMeasurementPeerSeparatorView?.isHidden = true
                    self.selectSpeedMeasurementPeerContainerView?.isHidden = true
                }
            })
        }
    }

    private func start() {
        deviceInfoView?.reset()

        // TODO: display error message if location permission is not granted
        locationTracker.start(updateLocationCallback: { location in
            DispatchQueue.main.async {
                self.deviceInfoView?.locationLabel?.text = location.coordinate.dmFormattedString
                self.deviceInfoView?.locationInfoLabel?.text = "\(R.string.localizable.homeLocation()) ±\(String(format: "%dm", Int(location.horizontalAccuracy)))"
            }
        })

        ipInfo = MEASUREMENT_AGENT.newIPConnectivityInfo()

        updateIpInfo()

        reachability = NetworkInfoReachability(whenReachable: { (type, details) in
            DispatchQueue.main.async {
                self.speedMeasurementGaugeView?.isStartButtonEnabled = true

                self.speedMeasurementGaugeView?.networkTypeLabel?.text = type
                self.speedMeasurementGaugeView?.networkDetailLabel?.text = details
            }
        }, whenUnreachable: {
            self.updateIpInfo()

            DispatchQueue.main.async {
                self.speedMeasurementGaugeView?.isStartButtonEnabled = false

                self.speedMeasurementGaugeView?.networkTypeLabel?.text = R.string.localizable.networkUnknown()
                self.speedMeasurementGaugeView?.networkDetailLabel?.text = R.string.localizable.networkNoConnection()
            }
        })
        reachability?.start()

        ipTimer = Repeater.every(.seconds(30)) { _ in
            self.updateIpInfo()
        }

        updateDeviceInfo()

        timer = Repeater.every(.seconds(1)) { _ in
            self.updateDeviceInfo()
        }
    }

    private func stop() {
        locationTracker.stop()

        timer?.removeAllObservers(thenStop: true)
        timer = nil

        reachability?.stop()
        reachability = nil

        ipTimer?.removeAllObservers(thenStop: true)
        ipTimer = nil

        ipInfo = nil
    }

    func displayPreMeasurementWarningAlert() {
        let alert = UIAlertController(
            title: R.string.localizable.homePreMeasurementAlertTitle(),
            message: R.string.localizable.homePreMeasurementAlertMessage(),
            preferredStyle: .alert
        )

        alert.addAction(UIAlertAction(title: R.string.localizable.homePreMeasurementAlertAbort(), style: .default))

        alert.addAction(UIAlertAction(title: R.string.localizable.homePreMeasurementAlertContinue(), style: .destructive) { _ in
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
                    self.deviceInfoView?.cpuValueLabel?.text = "\(R.string.localizable.generalNotAvailable())%"
                }
            }

            if let memoryUsage = self.memoryUsageInfo.getCurrentMemoryUsagePercent() {
                DispatchQueue.main.async {
                    self.deviceInfoView?.memValueLabel?.text = String(format: "%0.2f%%", memoryUsage * 100)
                }
            } else {
                DispatchQueue.main.async {
                    self.deviceInfoView?.cpuValueLabel?.text = "\(R.string.localizable.generalNotAvailable())%"
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
        logger.debug("updating ip status: \(String(describing: status))")

        var icon: IconFont = .cross
        var color = COLOR_IP_INFO_UNAVAILABLE

        if let status = status, status.hasInternetConnection {
            icon = .check
            if status.isNat {
                color = COLOR_IP_INFO_NAT
            } else {
                color = COLOR_IP_INFO_NO_NAT
            }
        }

        DispatchQueue.main.async {
            label?.icon = icon
            label?.textColor = color
        }
    }
}

extension HomeViewController: TermsAndConditionsDelegate {

    func didAcceptTermsAndConditions() {
        start()
    }
}
