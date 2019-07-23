// ios-app: CurrentMeasurementPeerTableViewController.swift, created on 23.07.19
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
class CurrentMeasurementPeerTableViewController: UITableViewController {

    @IBOutlet private var measurementPeerLabel: UILabel?

    var measurementPeers: [SpeedMeasurementPeerResponse.SpeedMeasurementPeer]? {
        didSet {
            if selectedMeasurementPeer == nil {
                selectedMeasurementPeer = measurementPeers?.filter { $0.defaultPeer }.first
            }
        }
    }

    var selectedMeasurementPeer: SpeedMeasurementPeerResponse.SpeedMeasurementPeer? {
        didSet {
            measurementPeerLabel?.text = selectedMeasurementPeer?.name
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }

        switch identifier {
        case R.segue.currentMeasurementPeerTableViewController.present_measurement_peer_selection.identifier:
            if let measurementPeerSelectionTableViewController = ((segue.destination as? UINavigationController)?.topViewController as? MeasurementPeerSelectionTableViewController) {

                measurementPeerSelectionTableViewController.measurementPeers = measurementPeers
                measurementPeerSelectionTableViewController.selectedMeasurementPeer = selectedMeasurementPeer
            }
        default: break
        }
    }

    @IBAction func unwindSave(_ segue: UIStoryboardSegue) {
        if let src = segue.source as? MeasurementPeerSelectionTableViewController {
            selectedMeasurementPeer = src.selectedMeasurementPeer
        }
    }
}
