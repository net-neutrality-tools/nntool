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
import nntool_shared_swift

class BaseInformationCollector: InformationCollector {

    private var timeBasedResult: TimeBasedResultDto?

    private var startNs: UInt64?

    deinit {
        stop()
    }

    func start(_ timeBasedResult: TimeBasedResultDto, startNs: UInt64) {
        self.timeBasedResult = timeBasedResult
        self.startNs = startNs
    }

    func stop() {

    }

    func collect(into timeBasedResult: TimeBasedResultDto) {

    }

    func currentRelativeTimeNs() -> UInt64? {
        guard let ns = startNs else {
            return nil
        }

        return TimeHelper.currentTimeNs() - ns
    }
}
