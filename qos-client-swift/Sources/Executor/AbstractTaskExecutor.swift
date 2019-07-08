/***************************************************************************
 * Copyright 2018-2019 alladin-IT GmbH
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
import nntool_shared_swift

///
public class AbstractTaskExecutor<C: AbstractTaskConfiguration, R: AbstractTaskResult>: Operation {

    ///
    let internalConfig: C

    ///
    var internalDelegate: TaskExecutorDelegate?

    ///
    var status: AbstractTaskResult.Status = .unknown

    ///
    public var result: R {
        let r = R()

        r.objectiveTimeoutNs = internalConfig.timeoutNs
        r.objectiveQoSTestUid = internalConfig.qosTestUid

        r.status = status

        r.taskType = taskType

        r.startTimeNs = startTimeNs
        r.durationNs = durationNs

        return r
    }

    ///
    var startTimeNs: UInt64?

    ///
    var durationNs: UInt64?

    ///
    var taskType: TaskType? {
        return nil
    }

    public init(config: C) {
        self.internalConfig = config
    }

    override public func start() {
        startTimeNs = TimeHelper.currentTimeNs()

        super.start()

        if let s = startTimeNs {
            durationNs = TimeHelper.currentTimeNs() - s
        }
    }

    /*func validateConfiguration() {
        
    }

    func prepareResult() {
        
    }*/
}

extension AbstractTaskExecutor: TaskExecutorProtocol {
    public typealias Configuration = C
    public typealias Result = R

    public final var config: C {
        return internalConfig
    }

    public final var delegate: TaskExecutorDelegate? {
        get {
            return internalDelegate
        }
        set {
            internalDelegate = newValue
        }
    }

    // result cannot be overriden if it is declared in an extension
    /*var result: R? {
        return nil
    }*/

    ///
    public func runTask(success successCallback: @escaping AbstractTaskExecutor.TaskExecutorSuccessCallback) {
        DispatchQueue.global(qos: .background).async {
            let result = self.runTask()

            DispatchQueue.main.async {
                successCallback(result)
            }
        }
    }

    ///
    public func runTask() -> R {
        let operationQueue = OperationQueue()

        operationQueue.addOperation(self)
        operationQueue.waitUntilAllOperationsAreFinished()
        //operationQueue.addOperations([self], waitUntilFinished: true)

        return result
    }
}
