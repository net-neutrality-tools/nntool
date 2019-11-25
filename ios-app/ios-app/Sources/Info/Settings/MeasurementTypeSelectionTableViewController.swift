// ios-app: MeasurementTypeSelectionTableViewController.swift, created on 20.11.19
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
class MeasurementTypeSelectionTableViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()
    }
}

extension MeasurementTypeSelectionTableViewController {

    override func numberOfSections(in tableView: UITableView) -> Int {
        return MEASUREMENT_AGENT.programs.count
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return MEASUREMENT_AGENT.programs[section].availableTasks.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: R.reuseIdentifier.program_task_tableviewcell.identifier, for: indexPath)

        let task = MEASUREMENT_AGENT.programs[indexPath.section].availableTasks[indexPath.row]

        cell.textLabel?.text = task.localizedName ?? task.name
        cell.detailTextLabel?.text = task.localizedDescription
        cell.detailTextLabel?.numberOfLines = 0
        cell.accessoryType = MEASUREMENT_AGENT.programs[indexPath.section].enabledTasks.contains(task.name) ? .checkmark : .none

        return cell
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return MEASUREMENT_AGENT.programs[indexPath.section].isEnabled ? UITableView.automaticDimension : 0
    }

    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return MEASUREMENT_AGENT.programs[section].name
    }

    override func tableView(_ tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        guard let header = view as? UITableViewHeaderFooterView else { return }
        guard let headerLabelFont = header.textLabel?.font else { return }

        var enableButton: UIButton?

        for i in 0..<view.subviews.count where view.subviews[i] is UIButton {
            enableButton = view.subviews[i] as? UIButton
        }

        if enableButton == nil {
            enableButton = UIButton(type: .system)
            header.addSubview(enableButton!)
        }

        enableButton?.frame = CGRect(x: view.frame.size.width - 85, y: view.frame.size.height - 28, width: 77, height: 26)
        enableButton?.tag = section
        enableButton?.setTitle(
            MEASUREMENT_AGENT.programs[section].isEnabled ?
                R.string.localizable.settingsMeasurementTypeSelectionDisable() :
                R.string.localizable.settingsMeasurementTypeSelectionEnable(),
            for: .normal)
        enableButton?.titleLabel?.font = UIFont(descriptor: headerLabelFont.fontDescriptor, size: 11)
        enableButton?.contentHorizontalAlignment = .right
        enableButton?.setTitleColor(tableView.tintColor, for: .normal)
        enableButton?.addControlEvent(.primaryActionTriggered) { control in
            guard let button = control as? UIButton else { return }

            let program = MEASUREMENT_AGENT.programs[section]

            if program.isEnabled {
                MEASUREMENT_AGENT.enableProgram(program.name, enable: false)
                button.setTitle(R.string.localizable.settingsMeasurementTypeSelectionEnable(), for: .normal)
            } else {
                MEASUREMENT_AGENT.enableProgram(program.name)
                button.setTitle(R.string.localizable.settingsMeasurementTypeSelectionDisable(), for: .normal)
            }

            self.tableView.beginUpdates()
            self.tableView.endUpdates()
        }
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let cell = tableView.cellForRow(at: indexPath) {
            let program = MEASUREMENT_AGENT.programs[indexPath.section]

            let task = program.availableTasks[indexPath.row]

            if program.isTaskEnabled(task.name) {
                MEASUREMENT_AGENT.enableProgramTask(program.name, taskName: task.name, enable: false)
                cell.accessoryType = .none
            } else {
                MEASUREMENT_AGENT.enableProgramTask(program.name, taskName: task.name)
                cell.accessoryType = .checkmark
            }

            self.tableView.beginUpdates()
            self.tableView.endUpdates()
        }

        tableView.deselectRow(at: indexPath, animated: true)
    }
}
