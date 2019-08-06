// MeasurementAgentKit: MemoryUsageInformationCollector.swift, created on 13.06.19
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

class MemoryUsageInformationCollector: BaseInformationCollector {

    private let memoryUsageInfo = MemoryUsageInfo()

    override func collect(into timeBasedResult: TimeBasedResultDto) {
        let result = PointInTimeValueDto<Double>()

        result.value = memoryUsageInfo.getCurrentMemoryUsagePercent()
        result.relativeTimeNs = currentRelativeTimeNs()

        logger.debug("MEM: \(result.value)")

        timeBasedResult.memUsage?.append(result)
    }
}
