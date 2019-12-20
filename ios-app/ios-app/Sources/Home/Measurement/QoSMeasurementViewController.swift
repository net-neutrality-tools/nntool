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
import QoSKit

///
class QoSMeasurementViewController: UITableViewController {

    private var qosTypeNameDict = [String: String]()

    override func viewDidLoad() {
        super.viewDidLoad()

        if let qosProgram = MEASUREMENT_AGENT.programs.filter({ $0.name.lowercased() == "qos" }).first {
            qosTypeNameDict = qosProgram.availableTasks.reduce(into: [String: String]()) {
                $0[$1.name.lowercased()] = $1.localizedName
            }
        }
    }

    var groups = [QoSTaskGroup]() {
        didSet {
            groupProgress = groups.reduce(into: [String: Float]()) { (result, group) in
                result[group.key] = 0
            }

            DispatchQueue.main.async {
                self.tableView.reloadData()
            }
        }
    }

    private var groupProgress: [String: Float]?

    ///
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    ///
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return groups.count
    }

    ///
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: R.reuseIdentifier.qos_group_progress_cell.identifier, for: indexPath)

        if let qosCell = cell as? QoSGroupProgressCell {
            let group = groups[indexPath.row]

            qosCell.groupName = qosTypeNameDict[group.key.lowercased()] ?? group.key
            qosCell.progress = groupProgress?[group.key] ?? 0
        }

        return cell
    }

    ////

    func updateProgress(progress: Double, forGroup group: QoSTaskGroup) {
        guard let groupIndex = groups.firstIndex(where: { $0 === group }) else {
            return
        }

        groupProgress?[group.key] = Float(progress)

        DispatchQueue.main.async {
            if let qosCell = self.tableView.cellForRow(at: IndexPath(row: groupIndex, section: 0)) as? QoSGroupProgressCell {
                qosCell.progress = Float(progress)
            }
        }
    }
}
