// ios-app: SettingsTableViewController.swift, created on 19.03.19
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
import ActionKit

///
class SettingsTableViewController: UITableViewController {

    @IBOutlet private var ipv4OnlySwitchCell: UISwitchTableViewCell?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

        bindSwitch(ipv4OnlySwitchCell?.uiSwitch, toSettingsKey: "nntool.general.ipv4only") { isOn in
            MEASUREMENT_AGENT.isIpv4Only = isOn
        }
    }

    private func bindSwitch(_ uiSwitch: UISwitch?, toSettingsKey key: String, onChange: ((Bool) -> Void)? = nil) {
        uiSwitch?.isOn = UserDefaults.standard.bool(forKey: key)
        uiSwitch?.addControlEvent(.valueChanged) { control in // (control: UIControl)
            guard let sw = control as? UISwitch else {
                return
            }

            logger.debug("Settings: Setting value \(sw.isOn) for key \(key)")
            UserDefaults.standard.set(sw.isOn, forKey: key)

            onChange?(sw.isOn)
        }
    }
}
