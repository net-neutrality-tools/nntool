// ios-app: HistoryTableViewController.swift, created on 19.03.19
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
import PaginatedTableView

///
class HistoryTableViewController: UIViewController {

    @IBOutlet private var tableView: PaginatedTableView?

    private var data: [BriefMeasurementResponse]?

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()

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
            if let measurementResultViewController = segue.destination as? MeasurementResultTableViewController {
                measurementResultViewController.measurementUuid = (sender as? BriefMeasurementResponse)?.uuid
                //measurementResultViewController.openDataUuid = (sender as? BriefMeasurementResponse)?.openDataUuid
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

        cell.dateLabel?.text = "n/a"
        cell.rttLabel?.text = "n/a"
        cell.downloadLabel?.text = "n/a"
        cell.uploadLabel?.text = "n/a"

        cell.technologyLabel?.text = item.networkTypeName ?? "n/a"

        if let startTime = item.startTime {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            dateFormatter.timeZone = TimeZone(secondsFromGMT: 0)

            cell.dateLabel?.text = dateFormatter.string(from: startTime)
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
                logger.debug(response.totalPages)
                logger.debug(pageNumber)
                logger.debug("MORE DATA?: \(response.totalPages != pageNumber)")
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

    /*override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
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
     }*/

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let item = data?[indexPath.row] else {
            return
        }

        performSegue(withIdentifier: R.segue.historyTableViewController.show_measurement_result_from_history.identifier, sender: item)
    }
}
