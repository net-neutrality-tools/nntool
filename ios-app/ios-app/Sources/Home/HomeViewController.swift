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

///
class HomeViewController: CustomNavigationBarViewController {

    @IBOutlet private var speedMeasurementGaugeView: SpeedMeasurementGaugeView?

    ///
    override func viewDidLoad() {
        super.viewDidLoad()

        // view controllers in tabBar should not be customizable
        tabBarController?.customizableViewControllers = []

        navigationItem.applyIconFontAttributes()

        speedMeasurementGaugeView?.startButtonActionCallback = {
            self.performSegue(withIdentifier: "show_speed_measurement_view_controller", sender: self)
        }
    }

    ///
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        if !MEASUREMENT_AGENT.isRegistered() {
            performSegue(withIdentifier: "present_modally_terms_and_conditions", sender: self)
        }
    }
}