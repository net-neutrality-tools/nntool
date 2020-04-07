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
import MeasurementAgentKit
import Speed
import nntool_shared_swift

///
class IASProgram: NSObject, ProgramProtocol {

    var programDelegate: ProgramDelegate?

    var delegate: IASProgramDelegate?

    var programConfiguration: ProgramConfiguration?

    let speed = Speed()

    let measurementFinishedSemaphore = DispatchSemaphore(value: 0)

    var measurementFailed = false

    var config: IasMeasurementConfiguration?

    var serverAddress: String?
    var serverPort: String?
    var encryption = false

    private var currentPhase = SpeedMeasurementPhase.initialize

    var result: [AnyHashable: Any]?

    private var relativeStartTimeNs: UInt64?

    private var interfaceTrafficDownloadStart: InterfaceTraffic?
    private var interfaceTrafficUploadStart: InterfaceTraffic?
    private var interfaceTrafficUploadEnd: InterfaceTraffic?

    let encoder = JSONEncoder()

    enum IASError: Error {
        case measurementError
    }

    // swiftlint:disable cyclomatic_complexity
    func run(startTimeNs: UInt64) throws -> SubMeasurementResult {
        let relativeStartTimeNs = TimeHelper.currentTimeNs() - startTimeNs

        speed.speedDelegate = self

        delegate?.iasMeasurement(self, didStartPhase: .initialize)

        speed.measurementLoad()

        ////

        if let addr = serverAddress, let port = serverPort {
            speed.targetsTld = ""
            speed.targets = [addr]
            speed.targetsPort = port
        } else {
            speed.targetsTld = "net-neutrality.tools"
            speed.targets = ["peer-ias-de-01-ipv4"]
            speed.targetsPort = "80"
        }

        speed.wss = encryption ? 1 : 0

        speed.platform = "mobile"
        speed.performRttMeasurement      = programConfiguration?.enabledTasks.contains("rtt") ?? true
        speed.performDownloadMeasurement = programConfiguration?.enabledTasks.contains("download") ?? true
        speed.performUploadMeasurement   = programConfiguration?.enabledTasks.contains("upload") ?? true
        speed.performRouteToClientLookup = false
        speed.performGeolocationLookup   = false

        // add speed classes (need to transform to Dictionary until Speed library works with objects)
        if let c = config {
            for dl in c.download {
                if let dlDict = try? JSONSerialization.jsonObject(with: encoder.encode(dl), options: []) {
                    speed.downloadClasses.add(dlDict)
                }
            }

            for ul in c.upload {
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

        let relativeEndTimeNs = TimeHelper.currentTimeNs() - startTimeNs

        let res = IasMeasurementResult()

        if measurementFailed {
            // TODO: reason
            //res.status = .failed
            //return res
            throw IASError.measurementError
        }

        guard let r = result else {
            // TODO: reason

            //res.status = .failed // or .aborted
            //return res
            throw IASError.measurementError
        }

        res.relativeStartTimeNs = relativeStartTimeNs
        res.relativeEndTimeNs = relativeEndTimeNs

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

            // TODO: these values should be calculated on the collector.
            res.rttInfo?.averageNs = rttInfo["average_ns"] as? UInt64
            res.rttInfo?.maximumNs = rttInfo["maximum_ns"] as? UInt64
            res.rttInfo?.medianNs = rttInfo["median_ns"] as? UInt64
            res.rttInfo?.minimumNs = rttInfo["minimum_ns"] as? UInt64
            res.rttInfo?.standardDeviationNs = rttInfo["standard_deviation_ns"] as? UInt64
            //

            res.rttInfo?.rtts = (rttInfo["rtts"] as? [[AnyHashable: Any]])?.map { RttDto(rttNs: $0["rtt_ns"] as? UInt64, relativeTimeNs: $0["relative_time_ns_measurement_start"] as? UInt64) }
        }

        res.connectionInfo = ConnectionInfoDto()

        if let lastDownloadInfo = (r["download_info"] as? [[AnyHashable: Any]])?.last {
            res.bytesDownload = lastDownloadInfo["bytes"] as? UInt64
            res.bytesDownloadIncludingSlowStart = lastDownloadInfo["bytes_including_slow_start"] as? UInt64
            res.durationDownloadNs = lastDownloadInfo["duration_ns"] as? UInt64

            res.connectionInfo?.actualNumStreamsDownload = lastDownloadInfo["num_streams_end"] as? Int
            res.connectionInfo?.requestedNumStreamsDownload = lastDownloadInfo["num_streams_start"] as? Int

            res.connectionInfo?.webSocketInfoDownload = createWebsocketInfoDto(lastDownloadInfo)
        }

        if let lastUploadInfo = (r["upload_info"] as? [[AnyHashable: Any]])?.last {
            res.bytesUpload = lastUploadInfo["bytes"] as? UInt64
            res.bytesUploadIncludingSlowStart = lastUploadInfo["bytes_including_slow_start"] as? UInt64
            res.durationUploadNs = lastUploadInfo["duration_ns"] as? UInt64

            res.connectionInfo?.actualNumStreamsUpload = lastUploadInfo["num_streams_end"] as? Int
            res.connectionInfo?.requestedNumStreamsUpload = lastUploadInfo["num_streams_start"] as? Int

            res.connectionInfo?.webSocketInfoUpload = createWebsocketInfoDto(lastUploadInfo)
        }

        if let timeInfo = r["time_info"] as? [AnyHashable: UInt64] {
            if let rttStart = timeInfo["rtt_start"] {
                res.relativeStartTimeRttNs = rttStart - startTimeNs // TODO: startTimeNs is not in unix timestamp
            }

            if let dlStart = timeInfo["download_start"] {
                res.relativeStartTimeDownloadNs = dlStart - startTimeNs // TODO: startTimeNs is not in unix timestamp
            }

            if let ulStart = timeInfo["upload_start"] {
                res.relativeStartTimeUploadNs = ulStart - startTimeNs // TODO: startTimeNs is not in unix timestamp
            }
        }

        res.connectionInfo?.address = serverAddress
        res.connectionInfo?.encrypted = encryption
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
    // swiftlint:enable cyclomatic_complexity

    func createWebsocketInfoDto(_ dict: [AnyHashable: Any]) -> WebSocketInfoDto {
        let wsInfo = WebSocketInfoDto()
        wsInfo.frameCount = dict["frame_count"] as? Int
        wsInfo.frameCountIncludingSlowStart = dict["frame_count_including_slow_start"] as? Int
        wsInfo.frameSize = dict["frame_size"] as? Int
        wsInfo.overhead = dict["overhead"] as? Int
        wsInfo.overheadIncludingSlowStart = dict["overhead_including_slow_start"] as? Int
        wsInfo.overheadPerFrame = dict["overhead_per_frame"] as? Int

        return wsInfo
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
            case "error":
                logger.debug("MEASUREMENT ERROR -> aborting")
                measurementFailed = true
                speed.measurementStop()
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

        delegate?.iasMeasurement(self, didFinishPhase: .initialize)

        speed.measurementStart()
        delegate?.iasMeasurement(self, didStartPhase: .rtt)
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
