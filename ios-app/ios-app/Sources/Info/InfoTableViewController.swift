// ios-app: InfoTableViewController.swift, created on 19.03.19
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
import MeasurementAgentKit
import MessageUI

///
class InfoTableViewController: UITableViewController {

    @IBOutlet private var websiteTableViewCell: UITableViewCell?
    @IBOutlet private var emailTableViewCell: UITableViewCell?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

        websiteTableViewCell?.detailTextLabel?.text = INFO_WEBSITE_URL
        emailTableViewCell?.detailTextLabel?.text = INFO_EMAIL
    }

    ///
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = super.tableView(tableView, cellForRowAt: indexPath)

        if indexPath.section == 0 {
            // We don't want to have separator lines on the first section's cell. Unfortunately, the only way to
            // achieve this is by using this workaround. At least this prevents us from having to draw the other
            // separator lines ourself.
            // See https://stackoverflow.com/questions/29006311/grouped-uitableview-remove-outer-separator-line
            Timer.scheduledTimer(withTimeInterval: 0.15, repeats: false) { _ in
                for subview in cell.subviews {
                    if subview != cell.contentView && subview.frame.width == cell.frame.width {
                        subview.removeFromSuperview()
                    }
                }
            }
        } else if indexPath.section == 2 {
            if indexPath.row == 0 {
                cell.detailTextLabel?.text = MEASUREMENT_AGENT.uuid ?? R.string.localizable.generalNotAvailable()
            } else if indexPath.row == 1 {
                cell.detailTextLabel?.text = BundleHelper.buildAppVersionInfoString() ?? R.string.localizable.generalNotAvailable()
            }
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard indexPath.section == 1 else {
            super.tableView(tableView, didSelectRowAt: indexPath)
            return
        }

        switch indexPath.row {
        case 0: // website tapped
            presentWebBrowserWithUrlString(INFO_WEBSITE_URL)
        case 1: // email tapped
            guard MFMailComposeViewController.canSendMail() else {
                break
            }

            let mc = MFMailComposeViewController()
            mc.mailComposeDelegate = self
            mc.setToRecipients([INFO_EMAIL])
            // TODO: Add app version string into message for convenience

            self.present(mc, animated: true, completion: nil)
        default: break
        }

        tableView.deselectRow(at: indexPath, animated: true)
    }

    // Copying of uuid and/or version is disabled because the app freezes on 'UIPasteboard.general.string = ...'.
    /*override func tableView(_ tableView: UITableView, shouldShowMenuForRowAt indexPath: IndexPath) -> Bool {
        return indexPath.section == 2
    }

    override func tableView(_ tableView: UITableView, canPerformAction action: Selector, forRowAt indexPath: IndexPath, withSender sender: Any?) -> Bool {
        return action == #selector(copy(_:))
    }

    override func tableView(_ tableView: UITableView, performAction action: Selector, forRowAt indexPath: IndexPath, withSender sender: Any?) {
        if action == #selector(copy(_:)) {
            switch indexPath.row {
            case 0: UIPasteboard.general.string = MEASUREMENT_AGENT.uuid
            case 1: UIPasteboard.general.string = BundleHelper.buildAppVersionInfoString()
            default: break
            }
        }
    }*/
}

///
extension InfoTableViewController: MFMailComposeViewControllerDelegate {

    ///
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        self.dismiss(animated: true, completion: nil)
    }
}
