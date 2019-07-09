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
import Repeat
import MeasurementAgentKit

///
class HomeViewController: CustomNavigationBarViewController {

    @IBOutlet private var speedMeasurementGaugeView: SpeedMeasurementGaugeView?
    @IBOutlet private var deviceInfoView: HomeScreenDeviceInfoView?

    private let cpuUsageInfo = CpuUsageInfo()
    private let memoryUsageInfo = MemoryUsageInfo()
    private let locationTracker = LocationTracker()

    private var timer: Repeater?

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        //deviceInfoView?.reset()

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

        locationTracker.start(updateLocationCallback: { location in
            DispatchQueue.main.async {
                self.deviceInfoView?.locationLabel?.text = location.coordinate.dmFormattedString
                self.deviceInfoView?.locationInfoLabel?.text = "Location ±\(String(format: "%dm", Int(location.horizontalAccuracy)))"
            }
        })

        timer = Repeater.every(.seconds(1)) { _ in
            if let cpuUsage = self.cpuUsageInfo.getCurrentCpuUsagePercent() {
                DispatchQueue.main.async {
                    self.deviceInfoView?.cpuValueLabel?.text = String(format: "%0.2f%%", cpuUsage * 100)
                }
            }

            if let memoryUsage = self.memoryUsageInfo.getCurrentMemoryUsagePercent() {
                DispatchQueue.main.async {
                    self.deviceInfoView?.memValueLabel?.text = String(format: "%0.2f%%", memoryUsage * 100)
                }
            }
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)

        locationTracker.stop()

        timer?.removeAllObservers(thenStop: true)
        timer = nil
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
}
