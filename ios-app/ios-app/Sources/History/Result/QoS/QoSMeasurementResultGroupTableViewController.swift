// ios-app: QoSMeasurementResultGroupTableViewController.swift, created on 05.08.19
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

class QoSMeasurementResultGroupTableViewController: UITableViewController {

    var qosType: QoSGroupResult?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.title = qosType?.localizedName

        tableView.estimatedRowHeight = 80
        tableView.rowHeight = UITableView.automaticDimension
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }

        switch identifier {
        case R.segue.qoSMeasurementResultGroupTableViewController.show_qos_detail.identifier:
            if let qosDetailTableViewController = segue.destination as? QoSMeasurementResultDetailTableViewController {
                if let row = sender as? Int {
                    qosDetailTableViewController.task = qosType?.tasks[row]
                }
            }
        default: break
        }
    }
}

// MARK: TableView

extension QoSMeasurementResultGroupTableViewController {

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 2
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return 1
        } else {
            return qosType?.tasks.count ?? 0
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let identifier = indexPath.section == 0 ? R.reuseIdentifier.qos_type_desc_cell.identifier : R.reuseIdentifier.qos_task_cell.identifier
        let cell = tableView.dequeueReusableCell(withIdentifier: identifier, for: indexPath)

        if indexPath.section == 0 {
            cell.textLabel?.text = qosType?.localizedDescription
        } else {
            if let task = qosType?.tasks[indexPath.row] {
                cell.textLabel?.text = task.title
                cell.detailTextLabel?.text = task.localizedDescription

                var accessoryLabel: UILabel?

                if task.isSuccessful() {
                    accessoryLabel = UILabel.createIconLabel(icon: .check, textColor: COLOR_CHECKMARK_GREEN)
                } else {
                    accessoryLabel = UILabel.createIconLabel(icon: .cross, textColor: COLOR_CHECKMARK_RED)
                }

                accessoryLabel?.sizeToFit()
                accessoryLabel?.numberOfLines = 0
                accessoryLabel?.textAlignment = .center

                cell.accessoryView = accessoryLabel
            }
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if section == 0 {
            return R.string.localizable.historyQosGroupDetails()
        } else {
            return R.string.localizable.historyQosGroupTasks()
        }
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard indexPath.section == 1 else {
            return
        }

        performSegue(withIdentifier: R.segue.qoSMeasurementResultGroupTableViewController.show_qos_detail.identifier, sender: indexPath.row)
    }
}
