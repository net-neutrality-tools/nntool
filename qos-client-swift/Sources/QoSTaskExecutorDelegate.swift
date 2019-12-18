/***************************************************************************
* Copyright 2017 appscape gmbh
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

public protocol QoSTaskExecutorDelegate: AnyObject {

    func taskExecutorDidStart(_ taskExecutor: QoSTaskExecutor, withTaskGroups groups: [QoSTaskGroup])

    func taskExecutorDidFail(_ taskExecutor: QoSTaskExecutor, withError error: Error?)

    func taskExecutorDidStop(_ taskExecutor: QoSTaskExecutor)

    func taskExecutorDidUpdateProgress(_ progress: Double, ofGroup group: QoSTaskGroup, totalProgress: Double)

    func taskExecutorDidFinishWithResult(_ result: [QoSTaskResult])
}
