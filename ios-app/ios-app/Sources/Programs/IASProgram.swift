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
    
    var delegate: IASProgramDelegate?

    let speed = Speed()
    
    let measurementFinishedSemaphore = DispatchSemaphore(value: 0)

    func run() throws -> [AnyHashable: Any] {
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

        // speed classes

        // dl low
        speed.downloadClasses.add([
            "default": false,
            "streams": 4,
            "frameSize": 2048,
            "bounds": [
                "lower": 0.01,
                "upper": 1.05
            ]
        ])

        // dl default
        speed.downloadClasses.add([
            "default": true,
            "streams": 4,
            "frameSize": 32768,
            "bounds": [
                "lower": 0.95,
                "upper": 525
            ]
        ])

        // dl high
        speed.downloadClasses.add([
            "default": true,
            "streams": 4,
            "frameSize": 524288,
            "bounds": [
                "lower": 475,
                "upper": 1050
            ]
        ])

        // dl very high
        speed.downloadClasses.add([
            "default": true,
            "streams": 8,
            "frameSize": 524288,
            "bounds": [
                "lower": 950,
                "upper": 9000
            ]
        ])

        // ul low
        speed.uploadClasses.add([
            "default": false,
            "streams": 4,
            "frameSize": 2048,
            "bounds": [
                "lower": 0.01,
                "upper": 1.05
            ]
        ])

        // ul default
        speed.uploadClasses.add([
            "default": true,
            "streams": 4,
            "frameSize": 32768,
            "bounds": [
                "lower": 0.95,
                "upper": 525
            ]
        ])

        // ul high
        speed.uploadClasses.add([
            "default": true,
            "streams": 4,
            "frameSize": 65535,
            "bounds": [
                "lower": 475,
                "upper": 1050
            ]
        ])

        // ul very high
        speed.uploadClasses.add([
            "default": true,
            "streams": 20,
            "frameSize": 65535,
            "bounds": [
                "lower": 950,
                "upper": 9000
            ]
        ])

        _ = measurementFinishedSemaphore.wait(timeout: DispatchTime.distantFuture) // TODO: result; timeout
        
        return [:]
    }
    
    private var currentPhase = SpeedMeasurementPhase.initialize
}

extension IASProgram: SpeedDelegate {
    
    func showKpisFromResponse(response: [AnyHashable: Any]!) {
        //print(response.description)

        print("showKpisFromResponse (delegate: \(delegate))")
        
        let newPhase = SpeedMeasurementPhase(rawValue: response["test_case"] as? String ?? "init") ?? currentPhase
        if newPhase != currentPhase {
            currentPhase = newPhase
            delegate?.iasMeasurement(self, didStartPhase: currentPhase)
        }
        
        print(response["cmd"])
        print(response["test_case"])
        print(response["msg"])
        
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
