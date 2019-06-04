// ios-app: QoSProgram.swift, created on 11.04.19
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
import QoSKit
import CodableJSON

///
class QoSProgram: ProgramProtocol {
    //typealias Delegate = QoSProgramDelegate

    var programDelegate: ProgramDelegate?

    //var delegate: QoSProgramDelegate?

    var forwardDelegate: QoSTaskExecutorDelegate?

    var objectives: [String: [[String: JSON]]]?

    let semaphore = DispatchSemaphore(value: 0)

    var result: [QoSTaskResult]?

    var qosTaskExecutor: QoSTaskExecutor?

    func run() throws -> SubMeasurementResult {
        let res = QoSMeasurementResult()
        
        guard let objtvs = objectives else {
            res.status = .failed
            return res // or throw?
        }

        qosTaskExecutor = QoSTaskExecutor()
        qosTaskExecutor?.delegate = self

        qosTaskExecutor?.startWithObjectives(objtvs, token: "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw=") // TODO

        print("before wait")

        semaphore.wait()

        print("after wait")

        guard let r = result else {
            res.status = .failed
            return res // or throw?
        }
        
        res.status = .finished
        
        res.objectiveResults = r
        print(res.objectiveResults)
        
        return res
    }

    func cancel() {
        qosTaskExecutor?.cancel()
    }
}

extension QoSProgram: QoSTaskExecutorDelegate {

    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup]) {
        print("QOS -- START with: \(groups)")

        forwardDelegate?.taskExecutorDidStart(taskExecutor, withTaskGroups: groups)
    }

    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?) {
        print("QOS -- ERROR: \(error)")

        forwardDelegate?.taskExecutorDidFail(taskExecutor, withError: error)

        //semaphore.signal()
    }

    func taskExecutorDidStop(_ taskExecutor: QoSTaskExecutor) {
        forwardDelegate?.taskExecutorDidStop(taskExecutor)
        semaphore.signal()
    }

    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double) {
        print("QOS -- UPDATE PROGRESS: \(progress), \(group), total: \(totalProgress)")

        forwardDelegate?.taskExecutorDidUpdateProgress(progress, ofGroup: group, totalProgress: totalProgress)
    }

    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult]) {
        print("QOS -- finish! \(result)")

        self.result = result

        forwardDelegate?.taskExecutorDidFinishWithResult(result)

        //semaphore.signal()
    }
}
