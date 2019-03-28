// ios-app: MeasurementViewController.swift, created on 25.03.19
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

///
class MeasurementViewController: CustomNavigationBarViewController {

    // TODO: QoS view controller is a child view controller

    @IBOutlet private var qosMeasurementViewController: QoSMeasurementViewController?
    
    @IBOutlet private var speedMeasurementGaugeView: SpeedMeasurementGaugeView?

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        hideNavigationItems()

        // TODO: disable naviation during measurement
        //tabBarController?.tabBar.items?.forEach { $0.isEnabled = false }
        
        startMeasurement()
    }
    
    ///
    private func startMeasurement() {
        //resetUI()
        
        
    }
}
