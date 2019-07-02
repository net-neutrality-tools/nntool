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

    var programDelegate: ProgramDelegate?

    var delegate: IASProgramDelegate?

    let speed = Speed()

    let measurementFinishedSemaphore = DispatchSemaphore(value: 0)

    var config: IasMeasurementConfiguration?

    private var currentPhase = SpeedMeasurementPhase.initialize

    var result: [AnyHashable: Any]?

    func run() throws -> SubMeasurementResult {
        speed.speedDelegate = self

        speed.measurementLoad()

        ////

        speed.platform = "mobile"

        speed.targets = ["peer-ias-de-01-ipv4"]
        speed.targetsRtt = ["peer-ias-de-01-ipv4"]
        speed.targetsPort = "80"

        speed.performRttMeasurement      = true
        speed.performDownloadMeasurement = true
        speed.performUploadMeasurement   = true
        speed.performRouteToClientLookup = false
        speed.performGeolocationLookup   = false

        //speed.parallelStreamsDownload = 4
        //speed.frameSizeDownload = 32768

        //speed.parallelStreamsUpload = 4
        //speed.frameSizeUpload = 32768

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

        let semaphoreResult = measurementFinishedSemaphore.wait(timeout: DispatchTime.distantFuture) // TODO: timeout
        if semaphoreResult == .timedOut {
            cancel()
            // TODO: mark measurement as timed out
        }

        let res = IasMeasurementResult()

        guard let r = result else {
            // TODO: status/reason

            res.status = .failed // or .aborted
            return res
        }

        if let rttInfo = r["rtt_info"] as? [AnyHashable: Any] {
            res.durationRttNs = rttInfo["duration_ns"] as? UInt64

            res.rttInfo = RttInfoDto()

            res.rttInfo?.numError = rttInfo["num_error"] as? Int
            res.rttInfo?.numMissing = rttInfo["num_missing"] as? Int
            res.rttInfo?.numReceived = rttInfo["num_received"] as? Int
            res.rttInfo?.numSent = rttInfo["num_sent"] as? Int
            res.rttInfo?.packetSize = rttInfo["packet_size"] as? Int
            res.rttInfo?.requestedNumPackets = 10 // TODO

            res.rttInfo?.rtts = [RttDto]() // TODO
        }

        res.connectionInfo = ConnectionInfoDto()

        if let lastDownloadInfo = (r["download_info"] as? [[AnyHashable: Any]])?.last {
            res.bytesDownload = lastDownloadInfo["bytes"] as? UInt64
            res.bytesDownloadIncludingSlowStart = lastDownloadInfo["bytes_including_slow_start"] as? UInt64
            res.durationDownloadNs = lastDownloadInfo["duration_ns_total"] as? UInt64
            //res.relativeStartTimeDownloadNs

            res.connectionInfo?.actualNumStreamsDownload = lastDownloadInfo["num_streams_end"] as? Int // TODO: this might not be correct
            //res.connectionInfo?.agentInterfaceDownloadMeasurementTraffic // TODO
            res.connectionInfo?.requestedNumStreamsDownload = lastDownloadInfo["num_streams_start"] as? Int // TODO: this might not be correct

            res.connectionInfo?.webSocketInfoDownload = WebSocketInfoDto()
            res.connectionInfo?.webSocketInfoDownload?.frameCount = lastDownloadInfo["frame_count"] as? Int
            res.connectionInfo?.webSocketInfoDownload?.frameCountIncludingSlowStart = lastDownloadInfo["frame_count_including_slow_start"] as? Int
            res.connectionInfo?.webSocketInfoDownload?.frameSize = lastDownloadInfo["frame_size"] as? Int
            res.connectionInfo?.webSocketInfoDownload?.overhead = lastDownloadInfo["overhead"] as? Int
            res.connectionInfo?.webSocketInfoDownload?.overheadIncludingSlowStart = lastDownloadInfo["overhead_including_slow_start"] as? Int
            res.connectionInfo?.webSocketInfoDownload?.overheadPerFrame = lastDownloadInfo["overhead_per_frame"] as? Int
        }

        if let lastUploadInfo = (r["upload_info"] as? [[AnyHashable: Any]])?.last {
            res.bytesUpload = lastUploadInfo["bytes"] as? UInt64
            res.bytesUploadIncludingSlowStart = lastUploadInfo["bytes_including_slow_start"] as? UInt64
            res.durationUploadNs = lastUploadInfo["duration_ns_total"] as? UInt64
            //res.relativeStartTimeUploadNs

            res.connectionInfo?.actualNumStreamsUpload = lastUploadInfo["num_streams_end"] as? Int // TODO: this might not be correct
            //res.connectionInfo?.agentInterfaceUploadMeasurementTraffic // TODO
            res.connectionInfo?.requestedNumStreamsUpload = lastUploadInfo["num_streams_start"] as? Int // TODO: this might not be correct

            res.connectionInfo?.webSocketInfoUpload = WebSocketInfoDto()
            res.connectionInfo?.webSocketInfoUpload?.frameCount = lastUploadInfo["frame_count"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.frameCountIncludingSlowStart = lastUploadInfo["frame_count_including_slow_start"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.frameSize = lastUploadInfo["frame_size"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.overhead = lastUploadInfo["overhead"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.overheadIncludingSlowStart = lastUploadInfo["overhead_including_slow_start"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.overheadPerFrame = lastUploadInfo["overhead_per_frame"] as? Int
        }

        if let timeInfo = r["time_info"] as? [AnyHashable: UInt64] {
            res.relativeStartTimeRttNs = timeInfo["rtt_start"]
            res.relativeStartTimeDownloadNs = timeInfo["download_start"]
            res.relativeStartTimeUploadNs = timeInfo["upload_start"]
        }

        //res.connectionInfo
        //res.reason
        //res.relativeEndTimeNs
        //res.relativeStartTimeNs
        //res.status

        // TODO

        res.status = .finished

        return res
    }

    func cancel() {
        speed.measurementStop()
    }
}

extension IASProgram: SpeedDelegate {

    func showKpisFromResponse(response: [AnyHashable: Any]!) {
        //logger.debug(response.description)

        logger.debug("showKpisFromResponse (delegate: \(delegate))")

        logger.debug(response["cmd"])
        logger.debug(response["test_case"])
        /*logger.debug(response["msg"])
        logger.debug("-----")
        logger.debug(response)
        logger.debug("-----")*/

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
                    case .rtt: return (response["rtt_info"] as? [AnyHashable: Any])?[/*"min_ns"*/"average_ns"] as? Double
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
                        let numError = rttInfo["num_error"] as? Int//,

                        /*let minNs = rttInfo["min_ns"] as? Double,
                        let maxNs = rttInfo["max_ns"] as? Double,
                        let medianNs = rttInfo["median_ns"] as? Double*/ {

                        // TODO: single rtt values
                        logger.debug("--!!-- RTT: \(rttInfo["min_ns"]), \(rttInfo["max_ns"]), \(rttInfo["median_ns"]), \(numReceived)")

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
                    logger.debug("PHASE_PROGRESS: \(phaseProgress)")
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
        //logger.debug(response)
        //logger.debug(error)

        logger.debug("measurementDidLoad")

        speed.measurementStart()
        delegate?.iasMeasurement(self, didStartPhase: .initialize)
    }

    func measurementCallback(withResponse response: [AnyHashable: Any]!) {
        showKpisFromResponse(response: response)

        logger.debug("measurementCallback")
    }

    func measurementDidComplete(withResponse response: [AnyHashable: Any]!, withError error: Error!) {
        showKpisFromResponse(response: response)
        logger.debug(error)

        logger.debug("FIN")

        result = response

        logger.debugExec {
            let res = try? String(data: JSONSerialization.data(withJSONObject: result, options: .prettyPrinted), encoding: .utf8)!
            logger.debug(res)
        }

        measurementFinishedSemaphore.signal()
    }

    func measurementDidStop() {
        logger.debug("STOP")

        measurementFinishedSemaphore.signal()
    }

    func measurementDidClearCache() {
        logger.debug("CLEAR CACHE")
    }
}
