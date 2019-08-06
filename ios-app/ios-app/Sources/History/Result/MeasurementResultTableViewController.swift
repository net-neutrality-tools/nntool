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

    private var data: DetailMeasurementResponse?
    private var qosData: FullQoSMeasurement?
    private var qosTypeBoxResult: [QoSMeasurementType: QoSTypeBoxResult]?

    private let qosCollectionViewFlowLayoutConfig = CollectionViewFlowLayoutConfig(
        sectionInsets: UIEdgeInsets(top: 10.0, left: 20.0, bottom: 10.0, right: 20.0),
        itemsPerRow: 2,
        heightFactor: 0.85
    )

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

        let nib = UINib(nibName: "GroupSectionHeaderView", bundle: nil)
        tableView.register(nib, forHeaderFooterViewReuseIdentifier: "group_section_header_view")

        if let measurementUuid = measurementUuid {
            MEASUREMENT_AGENT.resultService?.getDetailedMeasurementResult(measurementUuid: measurementUuid, onSuccess: { response in
                self.data = response

                // load qos
                // TODO: gather QoS results
                MEASUREMENT_AGENT.resultService?.getFullMeasurementResult(measurementUuid: measurementUuid, onSuccess: { fullMeasurementReponse in

                    if let fullQoSMeasurement = fullMeasurementReponse.measurements?["QOS"]?.content as? FullQoSMeasurement {
                        self.qosData = fullQoSMeasurement

                        // TODO: parse into model (type -> desc, with calculated success/error rate)

                        let qosGroup = DetailMeasurementGroup()
                        qosGroup.title = "QoS"
                        qosGroup.iconCharacter = IconFont.qos.rawValue

                        self.data?.groups?.append(qosGroup)

                        //

                        self.qosTypeBoxResult = [:]

                        self.qosData?.results?.forEach({ result in
                            guard let type = result.type else {
                                return
                            }

                            if self.qosTypeBoxResult?[type] == nil {
                                if let typeDescription = self.qosData?.qosTypeToDescriptionMap?[type.rawValue] {
                                    self.qosTypeBoxResult?[type] = QoSTypeBoxResult(
                                        type: type.rawValue, name: typeDescription.name ?? type.rawValue, icon: typeDescription.icon, successCount: 0, evaluationCount: 0
                                    )
                                }
                            }

                            self.qosTypeBoxResult?[type]?.successCount += result.successCount ?? 0
                            self.qosTypeBoxResult?[type]?.evaluationCount += result.evaluationCount ?? 0
                        })

                        DispatchQueue.main.async {
                            self.tableView.reloadData()
                        }
                    }
                }, onFailure: { error in
                    // TODO: handle error
                    logger.debug(error)
                })

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

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }

        switch identifier {
        case R.segue.measurementResultTableViewController.show_qos_group.identifier:
            if let qosGroupTableViewController = segue.destination as? QoSMeasurementResultGroupTableViewController {
                // TODO
                //logger.debug("tapped \((sender as? QoSGroupCollectionViewCell)?.groupNameLabel)")

                //qosGroupTableViewController.abc = ...
            }
        default: break
        }
    }
}

extension MeasurementResultTableViewController {

    override func numberOfSections(in tableView: UITableView) -> Int {
        return data?.groups?.count ?? 0
    }

    ///
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        guard let section = data?.groups?[section] else {
            return 0
        }

        if section.title == "QoS" {
            return 1
        }

        return section.items?.count ?? 0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let section = data?.groups?[indexPath.section] else {
            return super.tableView(tableView, cellForRowAt: indexPath)
        }

        if section.title == "QoS" {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: R.reuseIdentifier.qos_group_overview_cell.identifier, for: indexPath) as? QoSGroupOverviewCell else {
                return super.tableView(tableView, cellForRowAt: indexPath)
            }

            cell.evaluatedQoSResults = [QoSTypeBoxResult](qosTypeBoxResult!.values)
            cell.flowLayoutConfig = qosCollectionViewFlowLayoutConfig

            return cell
        }

        guard let item = section.items?[indexPath.row] else {
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

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        guard let section = data?.groups?[indexPath.section], section.title == "QoS" else {
            return super.tableView(tableView, heightForRowAt: indexPath)
        }

        return qosCollectionViewFlowLayoutConfig.getCollectionViewDimensions(viewWidth: tableView.frame.width, count: self.qosTypeBoxResult?.count ?? 0).height
    }

    //

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        guard let group = data?.groups?[section] else {
            return nil
        }

        guard let title = group.title else {
            return nil
        }

        guard let cell = tableView.dequeueReusableHeaderFooterView(withIdentifier: "group_section_header_view") as? GroupSectionHeaderView else {
            return nil
        }

        cell.iconLabel?.text = group.iconCharacter
        cell.titleLabel?.text = title.uppercased()
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
}
