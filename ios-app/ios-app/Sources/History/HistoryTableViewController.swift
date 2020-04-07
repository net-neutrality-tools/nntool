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
import MeasurementAgentKit

///
class HistoryTableViewController: UIViewController {

    @IBOutlet private var tableView: PaginatedTableView?

    private var data: [BriefMeasurementResponse]?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

        let headerNib = UINib(nibName: "HistoryListHeaderView", bundle: Bundle.main)
        tableView?.register(headerNib, forHeaderFooterViewReuseIdentifier: "history_list_header_view")

        tableView?.enablePullToRefresh = true

        tableView?.paginatedDelegate = self
        tableView?.paginatedDataSource = self
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)

        tableView?.loadData(refresh: true)
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }

        switch identifier {
        case R.segue.historyTableViewController.show_measurement_result_from_history.identifier:
            if let measurementResultViewController = segue.destination as? MeasurementResultTableViewController,
               let briefMeasurementResponse = sender as? BriefMeasurementResponse {

                measurementResultViewController.measurementUuid = briefMeasurementResponse.uuid
            }
        default: break
        }
    }
}

extension HistoryTableViewController: PaginatedTableViewDataSource {

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    ///
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data?.count ?? 0
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let item = data?[indexPath.row] else {
            fatalError("todo")
        }

        guard let cell = tableView.dequeueReusableCell(withIdentifier: R.reuseIdentifier.history_item_cell.identifier, for: indexPath) as? HistoryItemCell else {
            fatalError("todo")
        }

        let naString = R.string.localizable.generalNotAvailable()

        cell.dateLabel?.text = naString
        cell.rttLabel?.text = naString
        cell.downloadLabel?.text = naString
        cell.uploadLabel?.text = naString

        cell.technologyLabel?.text = item.networkTypeName ?? naString

        if let localTime = item.localTime {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = R.string.localizable.historyListDateFormat()
            cell.dateLabel?.text = dateFormatter.string(from: localTime)
        }

        if let iasResult = item.measurements?[MeasurementTypeDto.speed.rawValue]?.content as? BriefIASMeasurement {
            if let rttAvgNs = iasResult.rttAverageNs {
                cell.rttLabel?.text = String(format: "%0.2f", Double(rttAvgNs) / 1000.0 / 1000.0)
            }

            if let dlBps = iasResult.throughputAvgDownloadBps {
                cell.downloadLabel?.text = String(format: "%0.2f", Double(dlBps) / 1000.0 / 1000.0)
            }

            if let ulBps = iasResult.throughputAvgUploadBps {
                cell.uploadLabel?.text = String(format: "%0.2f", Double(ulBps) / 1000.0 / 1000.0)
            }
        }

        return cell
    }
}

extension HistoryTableViewController: PaginatedTableViewDelegate {

    func loadMore(_ pageNumber: Int, _ pageSize: Int, onSuccess: ((Bool) -> Void)?, onError: ((Error) -> Void)?) {
        MEASUREMENT_AGENT.resultService?.getMeasurements(page: pageNumber - 1, pageSize: pageSize, onSuccess: { response in
            if pageNumber == 1 {
                self.data = [BriefMeasurementResponse]()
            }

            if let newContent = response.content {
                let startIndex = response.pageSize * response.pageNumber

                for index in startIndex..<startIndex+min(newContent.count, response.pageSize) {
                    let element = newContent[index - startIndex]

                    if (self.data?.count ?? 0) <= index {
                        self.data?.insert(element, at: index)
                    } else {
                        self.data?[index] = element
                    }
                }
            }

            DispatchQueue.main.async { //After(deadline: .now() + 2.0) {
                //logger.debug(response.totalPages)
                //logger.debug(pageNumber)
                //logger.debug("MORE DATA?: \(response.totalPages != pageNumber)")
                onSuccess?(response.totalPages != pageNumber)
            }
        }, onFailure: { error in
            // TODO: error handling -> show error message
            DispatchQueue.main.async {
                onError?(error)
            }
        })
    }

    //

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 44
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return tableView.dequeueReusableHeaderFooterView(withIdentifier: "history_list_header_view")
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let item = data?[indexPath.row] else {
            return
        }

        performSegue(withIdentifier: R.segue.historyTableViewController.show_measurement_result_from_history.identifier, sender: item)
    }

    func tableView(_ tableView: UITableView, editActionsForRowAt indexPath: IndexPath) -> [UITableViewRowAction]? {
        guard let uuid = self.data?[indexPath.row].uuid else {
            return nil
        }

        let disassociateAction = UITableViewRowAction(
            style: .destructive,
            title: R.string.localizable.historyListDisassociateMeasurement()
        ) { (_, _) in
            MEASUREMENT_AGENT.resultService?.disassociateMeasurement(measurementUuid: uuid, onSuccess: { _ in
                DispatchQueue.main.async {
                    self.data?.remove(at: indexPath.row)
                    self.tableView?.deleteRows(at: [indexPath], with: .automatic)
                }
            }, onFailure: { error in
                logger.debug(error)
                // TODO: handle error
            })
        }

        return [disassociateAction]
    }
}
