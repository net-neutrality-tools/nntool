// ios-app: MeasurementResultTableViewController.swift, created on 23.07.19
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

class MeasurementResultTableViewController: UITableViewController {

    private let SUBTITLE_CELL_TEXT_COUNT_THRESHOLD = 20

    var measurementUuid: String?

    var openDataUuid: String?

    var data: DetailMeasurementResponse?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

        let nib = UINib(nibName: "GroupSectionHeaderView", bundle: nil)
        tableView.register(nib, forHeaderFooterViewReuseIdentifier: "group_section_header_view")

        if let measurementUuid = measurementUuid {
            MEASUREMENT_AGENT.resultService?.getDetailedMeasurementResult(measurementUuid: measurementUuid, onSuccess: { response in
                self.data = response

                //let qosGroup = DetailMeasurementGroup()
                //self.data?.groups?.append()

                DispatchQueue.main.async {
                    self.tableView.reloadData()
                }
            }, onFailure: { error in
                // TODO: error handling -> show error message
                logger.debug(error)
            })
        }
    }

    private func itemRequiresSubtitleCell(_ item: DetailMeasurementGroupItem) -> Bool {
        guard let title = item.title, let value = item.value else {
            return false
        }

        return title.count > SUBTITLE_CELL_TEXT_COUNT_THRESHOLD || value.count > SUBTITLE_CELL_TEXT_COUNT_THRESHOLD
    }

    @IBAction func shareMeasurementResult() {
        guard let textToShare = data?.shareMeasurementText else {
            return
        }

        let activityViewController = UIActivityViewController(activityItems: [textToShare], applicationActivities: nil)
        activityViewController.popoverPresentationController?.sourceView = self.view

        present(activityViewController, animated: true, completion: nil)
    }
}

extension MeasurementResultTableViewController {

    override func numberOfSections(in tableView: UITableView) -> Int {
        return data?.groups?.count ?? 0
    }

    ///
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data?.groups?[section].items?.count ?? 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let item = data?.groups?[indexPath.section].items?[indexPath.row] else {
            return super.tableView(tableView, cellForRowAt: indexPath)
        }

        let reuseIdentifier = (itemRequiresSubtitleCell(item) ? R.reuseIdentifier.subtitle_cell : R.reuseIdentifier.right_detail_cell).identifier

        let cell = tableView.dequeueReusableCell(withIdentifier: reuseIdentifier, for: indexPath)

        cell.textLabel?.text = item.title

        var value = item.value
        if let val = item.value, let unit = item.unit {
            value = "\(val) \(unit)"
        }

        cell.detailTextLabel?.text = value

        return cell
    }

    //

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        guard let group = data?.groups?[section] else {
            return nil
        }

        guard let cell = tableView.dequeueReusableHeaderFooterView(withIdentifier: "group_section_header_view") as? GroupSectionHeaderView else {
            return nil
        }

        cell.iconLabel?.text = group.iconCharacter
        cell.titleLabel?.text = group.title?.uppercased()
        cell.descriptionLabel?.text = group.description

        return cell
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        // TODO: calculate height of description
        if section == 0 {
            return 120
        }

        return 100
    }

    /*override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if let item = data?.groups?[indexPath.section].items?[indexPath.row] {
            //return
        }

        return super.tableView(tableView, heightForRowAt: indexPath)
    }*/

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}
