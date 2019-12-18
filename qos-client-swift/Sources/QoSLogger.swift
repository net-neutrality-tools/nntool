/***************************************************************************
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

import XCGLogger

class QoSLogger: XCGLogger {

    private let task: QoSTask

    init(task: QoSTask) {
        self.task = task

        super.init()

        setup(level: logger.outputLevel,
              showLogIdentifier: true,
              showFunctionName: true,
              showThreadName: false, //true,
              showLevel: true,
              showFileNames: true,
              showLineNumbers: true,
              showDate: true,
              writeToFile: nil,
              fileLevel: nil)
    }

    override func logln(_ level: XCGLogger.Level = .debug, functionName: String = #function, fileName: String = #file, lineNumber: Int = #line, userInfo: [String: Any] = [:], closure: () -> Any?) {

        super.logln(level, functionName: functionName, fileName: fileName, lineNumber: lineNumber, userInfo: userInfo) {
            guard let closureResult = closure() else { return nil }

            if let type = task.type {
                return "<\(type)|\(task.uid)|\(task.concurrencyGroup)> \(closureResult)"
            } else {
                return "<\(task.uid)|\(task.concurrencyGroup)> \(closureResult)"
            }
        }
    }
}
