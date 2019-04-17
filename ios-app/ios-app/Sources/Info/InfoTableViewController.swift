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

///
class InfoTableViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()
        //navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
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
        }

        return cell
    }
}
