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
import QoSKit
import CodableJSON
import nntool_shared_swift

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

    func run(startTimeNs: UInt64) throws -> SubMeasurementResult {
        let relativeStartTimeNs = TimeHelper.currentTimeNs() - startTimeNs
        let res = QoSMeasurementResult()

        guard let objtvs = objectives else {
            res.status = .failed
            return res // or throw?
        }

        qosTaskExecutor = QoSTaskExecutor()
        qosTaskExecutor?.delegate = self

        qosTaskExecutor?.startWithObjectives(objtvs, token: "bbd1ee96-0779-4619-b993-bb4bf7089754_1528136454_3gr2gw9lVhtVONV0XO62Vamu/uw=") // TODO

        logger.debug("before wait")

        semaphore.wait()

        let relativeEndTimeNs = TimeHelper.currentTimeNs() - startTimeNs

        logger.debug("after wait")

        guard let r = result else {
            res.status = .failed
            return res // or throw?
        }

        res.relativeStartTimeNs = relativeStartTimeNs
        res.relativeEndTimeNs = relativeEndTimeNs

        res.status = .finished

        res.objectiveResults = r
        logger.debug(res.objectiveResults)

        return res
    }

    func cancel() {
        qosTaskExecutor?.cancel()
    }
}

extension QoSProgram: QoSTaskExecutorDelegate {

    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup]) {
        logger.debug("QOS -- START with: \(groups)")

        forwardDelegate?.taskExecutorDidStart(taskExecutor, withTaskGroups: groups)
    }

    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?) {
        logger.debug("QOS -- ERROR: \(String(describing: error))")

        forwardDelegate?.taskExecutorDidFail(taskExecutor, withError: error)

        //semaphore.signal()
    }

    func taskExecutorDidStop(_ taskExecutor: QoSTaskExecutor) {
        forwardDelegate?.taskExecutorDidStop(taskExecutor)
        semaphore.signal()
    }

    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double) {
        logger.debug("QOS -- UPDATE PROGRESS: \(progress), \(group), total: \(totalProgress)")

        forwardDelegate?.taskExecutorDidUpdateProgress(progress, ofGroup: group, totalProgress: totalProgress)
    }

    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult]) {
        logger.debug("QOS -- finish! \(result)")

        self.result = result

        forwardDelegate?.taskExecutorDidFinishWithResult(result)

        //semaphore.signal()
    }
}
