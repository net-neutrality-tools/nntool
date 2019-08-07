// MeasurementAgentKit: CpuUsageInformationCollector.swift, created on 13.06.19
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

//ProcessInfo.processInfo.processorCount
//ProcessInfo.processInfo.activeProcessorCount

class CpuUsageInformationCollector: BaseInformationCollector {

    private let cpuUsageInfo = CpuUsageInfo()

    override func collect(into timeBasedResult: TimeBasedResultDto) {
        let result = PointInTimeValueDto<Double>()

        result.value = cpuUsageInfo.getCurrentCpuUsagePercent()
        result.relativeTimeNs = currentRelativeTimeNs()

        logger.debug("CPU: \(String(describing: result.value))")

        timeBasedResult.cpuUsage?.append(result)
    }
}
