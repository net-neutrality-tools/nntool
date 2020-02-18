/***************************************************************************
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
 ***************************************************************************/

import Foundation
import CodableJSON
import nntool_shared_swift
import MeasurementKit

class MeasurementKitTask: QoSTask {

    private static let taskNameDict = [
        "mkit_web_connectivity": "WebConnectivity",
        "mkit_dash": "Dash"
    ]

    private var input: String?

    private var taskResult: JSON?
    private var downloadedKb: Double?
    private var uploadedKb: Double?

    ///
    enum CodingKeys4: String, CodingKey {
        case input
        case flags
    }

    override var statusKey: String? {
        return "mkit_status"
    }

    override var result: QoSTaskResult {
        var r = super.result

        r["mkit_result"] = taskResult
        r["mkit_downloaded_kb"] = JSON(downloadedKb)
        r["mkit_uploaded_kb"] = JSON(uploadedKb)

        return r
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys4.self)

        input = try container.decodeIfPresent(String.self, forKey: .input)

        try super.init(from: decoder)
    }

    private func getConfig(_ mkitTaskName: String) -> [String: Any] {
        // parse input
        var inputDict: [String] = []
        if let d = input?.data(using: .utf8) {
            do {
                if let dict = try JSONSerialization.jsonObject(with: d, options: .allowFragments) as? [String] {
                    inputDict = dict
                }
            } catch {
                taskLogger.error(error)
            }
        }

        //taskLogger.debug(inputDict)

        // https://github.com/measurement-kit/measurement-kit/tree/master/include/measurement_kit
        return [
            "name": mkitTaskName,
            /*"disabled_events": [
            
            ],*/
            "inputs": inputDict,
            "options": [
                //"net/ca_bundle_path":
                //"geoip_country_path":
                //"geoip_asn_path":
                "no_file_report": true,
                "no_collector": true,
                //"no_bouncer": true,
                "no_geoip": true,
                "no_resolver_lookup": true

            ],
            "log_level": "INFO"
        ]
    }

    private func deserializeEvent(_ event: [AnyHashable: Any]) -> MeasurementKitEvent? {
        do {
            let data = try JSONSerialization.data(withJSONObject: event, options: [])
            return try JSONDecoder().decode(MeasurementKitEvent.self, from: data)
        } catch {
            taskLogger.error(error)
            return nil
        }
    }

    private func postProcess() {
        // Some MeasurementKit tests return the whole body of a web page or API result.
        // We don't need this much data and remove it.
        if let requests = taskResult?["test_keys"]?["requests"]?.arrayValue {
            for i in 0..<requests.count {
                taskResult?["test_keys"]?["requests"]?[i]?["response"]?["body"] = JSON.null
            }
        }
    }

    override func taskMain() {
        guard let t = type else {
            taskLogger.debug("type nil")
            status = .error
            return
        }

        guard let mkitTaskName = MeasurementKitTask.taskNameDict[t.lowercased()] else {
            taskLogger.debug("mkitTaskName not found")
            status = .error
            return
        }

        guard let task = MKAsyncTaskWrapper.start(getConfig(mkitTaskName)) else {
            taskLogger.debug("async task start failed")
            status = .error
            return
        }

        let semaphore = DispatchSemaphore(value: 0)
        
        DispatchQueue.global(qos: .background).async {
            while !task.done() {
                guard !self.isCancelled else {
                    task.interrupt()
                    self.status = .error // TODO: add status for aborted state?
                    break
                }

                guard let event = task.waitForNextEvent() else {
                    self.taskLogger.debug("no event")
                    self.status = .error
                    break
                }

                //taskLogger.debug(event)

                guard let eventObj = self.deserializeEvent(event) else {
                    self.taskLogger.debug("cannot deserialize event")
                    self.status = .error
                    break
                }

                guard let eventType = eventObj.type else {
                    self.taskLogger.debug("no/unknown event type")
                    continue
                }

                switch eventType {
                case .measurement:
                    if let jsonData = eventObj.value.jsonString?.data(using: .utf8) {
                        do {
                            self.taskResult = try JSONDecoder().decode(JSON.self, from: jsonData)
                        } catch {
                            self.taskLogger.error(error)
                        }
                    }

                    if self.taskResult == nil {
                        self.taskResult = JSON(eventObj.value.jsonString)
                    }

                    self.status = .ok
                case .progress:
                    if let p = eventObj.value.percentage {
                        self.progress.completedUnitCount = Int64(p * 100)
                    }
                case .failureStartup, .failureReportCreate, .failureMeasurementSubmission:
                    self.status = .error
                case .statusEnd:
                    self.downloadedKb = eventObj.value.downloadedKb
                    self.uploadedKb = eventObj.value.uploadedKb
                case .log:
                    self.taskLogger.debug(eventObj.value.message)
                default:
                    //taskLogger.debug(eventType.rawValue)
                    break
                }
            }
            
            semaphore.signal()
        }
        
        let semaphoneTimeout = DispatchTime.now() + DispatchTimeInterval.nanoseconds(Int(truncatingIfNeeded: timeoutNs))
        if semaphore.wait(timeout: semaphoneTimeout) == .timedOut {
            task.interrupt()
            status = .timeout
        }

        postProcess()
    }
}
