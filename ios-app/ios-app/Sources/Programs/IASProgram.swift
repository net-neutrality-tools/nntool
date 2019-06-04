// ios-app: IASProgram.swift, created on 11.04.19
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
import MeasurementAgentKit
import Speed

///
class IASProgram: NSObject, ProgramProtocol {
    //typealias Delegate = IASProgramDelegate

    //var delegate: /*IASProgramDelegate*/Any?

    var programDelegate: ProgramDelegate?

    var delegate: IASProgramDelegate?

    let speed = Speed()

    let measurementFinishedSemaphore = DispatchSemaphore(value: 0)

    var config: IasMeasurementConfiguration?

    private var currentPhase = SpeedMeasurementPhase.initialize

    var result: [AnyHashable: Any]?

    func run() throws -> SubMeasurementResult {
        speed.speedDelegate = self

        //let tool = Tool()

        speed.measurementLoad()

        ////

        speed.platform = "mobile"

        speed.targets = ["peer-ias-de-01-ipv4"]
        speed.targetsRtt = ["peer-ias-de-01-ipv4"]

        speed.performRttMeasurement      = true
        speed.performDownloadMeasurement = true
        speed.performUploadMeasurement   = true
        speed.performRouteToClientLookup = false
        speed.performGeolocationLookup   = false

        speed.parallelStreamsDownload = 4
        speed.frameSizeDownload = 32768

        speed.parallelStreamsUpload = 4
        speed.frameSizeUpload = 32768

        let res = IasMeasurementResult()
        
        let encoder = JSONEncoder()

        // add speed classes (need to transform to Dictionary until Speed library works with objects)
        if let c = config {
            for dl in c.download {
                if let dlDict = try? JSONSerialization.jsonObject(with: encoder.encode(dl), options: []) {
                    speed.downloadClasses.add(dlDict)
                }
            }

            for ul in c.download {
                if let ulDict = try? JSONSerialization.jsonObject(with: encoder.encode(ul), options: []) {
                    speed.uploadClasses.add(ulDict)
                }
            }
        }

        _ = measurementFinishedSemaphore.wait(timeout: DispatchTime.distantFuture) // TODO: result; timeout

        guard let r = result else {
            return res
        }

        // TODO
        
        return res
    }

    func cancel() {
        speed.measurementStop()
    }
}

extension IASProgram: SpeedDelegate {

    func showKpisFromResponse(response: [AnyHashable: Any]!) {
        //print(response.description)

        print("showKpisFromResponse (delegate: \(delegate))")

        print(response["cmd"])
        print(response["test_case"])
        /*print(response["msg"])
        print("-----")
        print(response)
        print("-----")*/

        if let cmd = response["cmd"] as? String {
            switch cmd {
            case "info":
                let newPhase = SpeedMeasurementPhase(rawValue: response["test_case"] as? String ?? "init") ?? currentPhase
                if newPhase != currentPhase {
                    currentPhase = newPhase
                    delegate?.iasMeasurement(self, didStartPhase: currentPhase)
                }
            case "report":
                if let primaryValue: Double = {
                    switch currentPhase {
                    case .rtt: return (response["rtt_info"] as? [AnyHashable: Any])?["min_ns"] as? Double
                    case .download: return (response["download_info"] as? [[AnyHashable: Any]])?.last?["throughput_avg_bps"] as? Double
                    case .upload: return (response["upload_info"] as? [[AnyHashable: Any]])?.last?["throughput_avg_bps"] as? Double
                    default:
                        return nil
                    }
                    }() {
                    delegate?.iasMeasurement(self, didMeasurePrimaryValue: primaryValue, inPhase: currentPhase)
                }

                var phaseProgress: Double?

                switch currentPhase {
                case .initialize:
                    break
                case .rtt:
                    if  let rttInfo = response["rtt_info"] as? [String: Any],
                        let numReceived = rttInfo["num_received"] as? Int,
                        let numMissing = rttInfo["num_missing"] as? Int,
                        let numError = rttInfo["num_error"] as? Int {

                        phaseProgress = Double(numReceived + numMissing + numError) / 10.0 // TODO: rtt config packet count?
                    }
                case .download:
                    if  let downloadInfo = response["download_info"] as? [[String: Any]],
                        let lastDownloadInfo = downloadInfo.last,
                        let durationNs = lastDownloadInfo["duration_ns"] as? Double {

                        phaseProgress = (durationNs/* / Double(NSEC_PER_MSEC)*/) / (10 * Double(NSEC_PER_SEC))/*self.speed.measureTime.doubleValue*/
                    }
                case .upload:
                    if  let uploadInfo = response["upload_info"] as? [[String: Any]],
                        let lastUploadInfo = uploadInfo.last,
                        let durationNs = lastUploadInfo["duration_ns"] as? Double {

                        phaseProgress = (durationNs/* / Double(NSEC_PER_MSEC)*/) / (10 * Double(NSEC_PER_SEC))/*self.speed.measureTime.doubleValue*/
                    }
                }

                if let pp = phaseProgress {
                    print("PHASE_PROGRESS: \(phaseProgress)")
                    delegate?.iasMeasurement(self, didUpdateProgress: pp, inPhase: currentPhase)
                }
            case "finish":
                delegate?.iasMeasurement(self, didFinishPhase: currentPhase)
            default:
                break
            }
        }
    }

    func measurementDidLoad(withResponse response: [AnyHashable: Any]!, withError error: Error!) {
        //print(response)
        //print(error)

        print("measurementDidLoad")

        speed.measurementStart()
        delegate?.iasMeasurement(self, didStartPhase: .initialize)
    }

    func measurementCallback(withResponse response: [AnyHashable: Any]!) {
        showKpisFromResponse(response: response)

        print("measurementCallback")
    }

    func measurementDidComplete(withResponse response: [AnyHashable: Any]!, withError error: Error!) {
        showKpisFromResponse(response: response)
        print(error)

        print("FIN")

        result = response

        measurementFinishedSemaphore.signal()
    }

    func measurementDidStop() {
        print("STOP")

        measurementFinishedSemaphore.signal()
    }

    func measurementDidClearCache() {
        print("CLEAR CACHE")
    }
}
