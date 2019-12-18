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

typealias QoSTaskInitializer = (QoSTaskConfiguration) -> (QoSTask?)

// TODO: rewrite -> use factory?
// -> with "register type" approach?

public class QoSTaskGroup {

    private(set) public var key: String

    class func groupForKey(_ key: String) -> QoSTaskGroup? {
        return QoSTaskGroup(key: key)
    }

    init(key: String) {
        self.key = key
    }

    func taskWithConfiguration(config: QoSTaskConfiguration) -> QoSTask? {
        let task = TaskType(rawValue: key.lowercased())?.taskClass().create(config: config)
        task?.group = self

        return task
    }
}
