// ios-app: MeasurementPeerSelectionTableViewController.swift, created on 16.04.19
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
class MeasurementPeerSelectionTableViewController: UITableViewController {

    var measurementPeers: [SpeedMeasurementPeerResponse.SpeedMeasurementPeer]?

    var selectedMeasurementPeer: SpeedMeasurementPeerResponse.SpeedMeasurementPeer?

    @IBAction func cancel() {
        dismiss(animated: true, completion: nil)
    }

    // MARK: - TableView

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return measurementPeers?.count ?? 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: R.reuseIdentifier.measurement_peer_cell.identifier, for: indexPath)

        if let peer = measurementPeers?[indexPath.row] {
            cell.textLabel?.text = peer.name
            cell.detailTextLabel?.text = peer.description

            if peer.identifier == selectedMeasurementPeer?.identifier {
                cell.accessoryType = .checkmark
            } else {
                cell.accessoryType = .none
            }
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedMeasurementPeer = measurementPeers?[indexPath.row]

        tableView.deselectRow(at: indexPath, animated: true)
        tableView.reloadData()
    }
}
