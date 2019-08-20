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
import nntool_shared_swift

///
class IASProgram: NSObject, ProgramProtocol {

    var programDelegate: ProgramDelegate?

    var delegate: IASProgramDelegate?

    let speed = Speed()

    let measurementFinishedSemaphore = DispatchSemaphore(value: 0)

    var config: IasMeasurementConfiguration?

    var serverAddress: String?
    var serverPort: String?

    private var currentPhase = SpeedMeasurementPhase.initialize

    var result: [AnyHashable: Any]?

    private var relativeStartTimeNs: UInt64?

    private var interfaceTrafficDownloadStart: InterfaceTraffic?
    private var interfaceTrafficUploadStart: InterfaceTraffic?
    private var interfaceTrafficUploadEnd: InterfaceTraffic?

    func run(relativeStartTimeNs: UInt64) throws -> SubMeasurementResult {
        self.relativeStartTimeNs = relativeStartTimeNs

        speed.speedDelegate = self

        speed.measurementLoad()

        ////

        speed.platform = "mobile"

        if let addr = serverAddress, let port = serverPort {
            speed.targetsTld = ""
            speed.targets = [addr]
            speed.targetsRtt = [addr]
            speed.targetsPort = port
        } else {
            speed.targetsTld = "net-neutrality.tools"
            speed.targets = ["peer-ias-de-01-ipv4"]
            speed.targetsRtt = ["peer-ias-de-01-ipv4"]
            speed.targetsPort = "80"
        }

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

        let semaphoreResult = measurementFinishedSemaphore.wait(timeout: DispatchTime.distantFuture)
        if semaphoreResult == .timedOut {
            cancel()
            // TODO: mark measurement as timed out
        }

        let relativeEndTimeNs = TimeHelper.currentTimeNs() - relativeStartTimeNs

        let res = IasMeasurementResult()

        guard let r = result else {
            // TODO: status
            // TODO: reason

            res.status = .failed // or .aborted
            return res
        }

        res.relativeStartTimeNs = relativeStartTimeNs
        res.relativeEndTimeNs = relativeEndTimeNs

        // TODO

        res.status = .finished

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

            res.connectionInfo?.actualNumStreamsDownload = lastDownloadInfo["num_streams_end"] as? Int
            res.connectionInfo?.requestedNumStreamsDownload = lastDownloadInfo["num_streams_start"] as? Int

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

            res.connectionInfo?.actualNumStreamsUpload = lastUploadInfo["num_streams_end"] as? Int
            res.connectionInfo?.requestedNumStreamsUpload = lastUploadInfo["num_streams_start"] as? Int

            res.connectionInfo?.webSocketInfoUpload = WebSocketInfoDto()
            res.connectionInfo?.webSocketInfoUpload?.frameCount = lastUploadInfo["frame_count"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.frameCountIncludingSlowStart = lastUploadInfo["frame_count_including_slow_start"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.frameSize = lastUploadInfo["frame_size"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.overhead = lastUploadInfo["overhead"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.overheadIncludingSlowStart = lastUploadInfo["overhead_including_slow_start"] as? Int
            res.connectionInfo?.webSocketInfoUpload?.overheadPerFrame = lastUploadInfo["overhead_per_frame"] as? Int
        }

        if let timeInfo = r["time_info"] as? [AnyHashable: UInt64] {
            if let rttStart = timeInfo["rtt_start"] {
                res.relativeStartTimeRttNs = rttStart - relativeStartTimeNs
            }

            if let dlStart = timeInfo["download_start"] {
                res.relativeStartTimeDownloadNs = dlStart - relativeStartTimeNs
            }

            if let ulStart = timeInfo["upload_start"] {
                res.relativeStartTimeUploadNs = ulStart - relativeStartTimeNs
            }
        }

        res.connectionInfo?.address = serverAddress
        //res.connectionInfo?.encrypted
        //res.connectionInfo?.encryptionInfo
        if let serverPort = serverPort, let port = UInt16(serverPort) {
            res.connectionInfo?.port = port
        }
        //res.connectionInfo?.serverMss
        //res.connectionInfo?.serverMtu
        //res.connectionInfo?.tcpOptSackRequested
        //res.connectionInfo?.tcpOptWscaleRequested

        if let dlStartTraffic = interfaceTrafficDownloadStart {
            if let ulStartTraffic = interfaceTrafficUploadStart {
                res.connectionInfo?.agentInterfaceDownloadMeasurementTraffic = TrafficDto.fromInterfaceTraffic(ulStartTraffic.differenceTo(dlStartTraffic))
            }
        }

        if let ulStartTraffic = interfaceTrafficUploadStart, let ulEndTraffic = interfaceTrafficUploadEnd {
            res.connectionInfo?.agentInterfaceUploadMeasurementTraffic = TrafficDto.fromInterfaceTraffic(ulEndTraffic.differenceTo(ulStartTraffic))
        }

        if let dlStartTraffic = interfaceTrafficDownloadStart, let ulEndTraffic = interfaceTrafficUploadEnd {
            res.connectionInfo?.agentInterfaceTotalTraffic = TrafficDto.fromInterfaceTraffic(ulEndTraffic.differenceTo(dlStartTraffic))
        }

        return res
    }

    func cancel() {
        speed.measurementStop()
    }
}

extension IASProgram: SpeedDelegate {

    func showKpisFromResponse(response: [AnyHashable: Any]!) {
        //logger.debug(response.description)

        logger.debug("showKpisFromResponse (delegate: \(String(describing: delegate)))")

        logger.debug(response["cmd"])
        logger.debug(response["test_case"])
        /*logger.debug(response["msg"])
        logger.debug("-----")
        logger.debug(response)
        logger.debug("-----")*/

        if let cmd = response["cmd"] as? String {
            switch cmd {
            case "info": handleCmdInfo(response)
            case "report": handleCmdReport(response)
            case "finish": handleCmdFinish(response)
            default: break
            }
        }
    }

    private func handleCmdInfo(_ response: [AnyHashable: Any]!) {
        let newPhase = SpeedMeasurementPhase(rawValue: response["test_case"] as? String ?? "init") ?? currentPhase
        if newPhase != currentPhase {
            currentPhase = newPhase

            switch newPhase {
            case .download: interfaceTrafficDownloadStart = InterfaceTrafficInfo.getWwanAndWifiNetworkInterfaceTraffic()
            case .upload: interfaceTrafficUploadStart = InterfaceTrafficInfo.getWwanAndWifiNetworkInterfaceTraffic()
            default: break
            }

            delegate?.iasMeasurement(self, didStartPhase: currentPhase)
        }
    }

    private func handleCmdReport(_ response: [AnyHashable: Any]!) {
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
                    logger.debug("--!!-- RTT: \(String(describing: rttInfo["min_ns"])), \(String(describing: rttInfo["max_ns"])), \(String(describing: rttInfo["median_ns"])), \(numReceived)")

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
            logger.debug("PHASE_PROGRESS: \(String(describing: phaseProgress))")
            delegate?.iasMeasurement(self, didUpdateProgress: pp, inPhase: currentPhase)
        }
    }

    private func handleCmdFinish(_ response: [AnyHashable: Any]!) {
        switch currentPhase {
        case .upload: interfaceTrafficUploadEnd = InterfaceTrafficInfo.getWwanAndWifiNetworkInterfaceTraffic()
        default: break
        }

        delegate?.iasMeasurement(self, didFinishPhase: currentPhase)
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
