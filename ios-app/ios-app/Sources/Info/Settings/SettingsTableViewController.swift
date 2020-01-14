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

    private func presentDeleteAgentConfirmPopup() {
       let alert = UIAlertController.createBasicAlert(
            title: R.string.localizable.settingsDeleteAgentAlertTitle(),
            message: R.string.localizable.settingsDeleteAgentAlertMessage(),
            cancelActionTitle: R.string.localizable.settingsDeleteAgentAlertCancel(),
            confirmActionTitle: R.string.localizable.settingsDeleteAgentAlertConfirm(),
            confirmStyle: .destructive) { _ in

            self.deleteAgent()
        }

        present(alert, animated: true, completion: nil)
    }

    private func deleteAgent() {
        let progressAlert = UIAlertController.createLoadingAlert(title: R.string.localizable.settingsPopupDeleteAgent())
        self.present(progressAlert, animated: true, completion: nil)

        MEASUREMENT_AGENT.deleteAgent(success: {
            DispatchQueue.main.async {
                progressAlert.dismiss(animated: true) {
                    let parent = self.parent
                    self.navigationController?.popViewController(animated: false)
                    parent?.tabBarController?.selectedIndex = 0
                }
            }
        }, failure: {
            logger.debug("disassociate failed")

            DispatchQueue.main.async {
                progressAlert.dismiss(animated: true) {
                    self.presentDeleteAgentErrorPopup()
                }
            }
        })
    }

    private func presentDeleteAgentErrorPopup() {
        let alert = UIAlertController.createBasicAlert(
            title: R.string.localizable.settingsDeleteAgentErrorAlertTitle(),
            message: R.string.localizable.settingsDeleteAgentErrorAlertMessage(),
            cancelActionTitle: R.string.localizable.settingsDeleteAgentErrorAlertCancel(),
            confirmActionTitle: R.string.localizable.settingsDeleteAgentErrorAlertRetry(),
            confirmStyle: .destructive) { _ in

            self.deleteAgent()
        }

        present(alert, animated: true, completion: nil)
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard indexPath.section == 2 else {
            return
        }

        presentDeleteAgentConfirmPopup()

        tableView.deselectRow(at: indexPath, animated: true)
    }
}
