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

///
class InfoTableViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()
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
                let (versionStr, versionInt, buildDate) = BundleHelper.getAppVersionInfo()
                let gitInfo = BundleHelper.getBundleGitInfoString()

                if let vs = versionStr, let vi = versionInt, let bd = buildDate, let gi = gitInfo {
                    cell.detailTextLabel?.text = "\(vs) (\(vi), \(bd)), \(gi)"
                } else {
                    cell.detailTextLabel?.text = R.string.localizable.generalNotAvailable()
                }
            }
        }

        return cell
    }

    /*override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.section == 1 {
            if indexPath.row == 0 { // website tapped
                
                
                return
            } else if indexPath.row == 1 { // email tapped
                
                return
            }
        }
        
        super.tableView(tableView, didSelectRowAt: indexPath)
    }*/

    /*override func tableView(_ tableView: UITableView, shouldShowMenuForRowAt indexPath: IndexPath) -> Bool {
        return indexPath.section == 2
    }
    
    override func tableView(_ tableView: UITableView, canPerformAction action: Selector, forRowAt indexPath: IndexPath, withSender sender: Any?) -> Bool {
        return action == #selector(copy(_:))
    }
    
    override func tableView(_ tableView: UITableView, performAction action: Selector, forRowAt indexPath: IndexPath, withSender sender: Any?) {
        if action == #selector(copy(_:)) {
            UIPasteboard.general.string = MEASUREMENT_AGENT.uuid
        }
    }*/
}
