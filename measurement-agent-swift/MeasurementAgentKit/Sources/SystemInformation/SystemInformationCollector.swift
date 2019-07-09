// MeasurementAgentKit: SystemInformationCollector.swift, created on 13.06.19
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
import Repeat

//ProcessInfo.processInfo.thermalState
//ProcessInfo.processInfo.isLowPowerModeEnabled
//ProcessInfo.processInfo.systemUptime

class SystemInformationCollector {

    private var collectors = [InformationCollector]()

    class func defaultCollector() -> SystemInformationCollector {
        let collector = SystemInformationCollector()

        collector.registerCollector(CpuUsageInformationCollector())
        collector.registerCollector(MemoryUsageInformationCollector())
        collector.registerCollector(GpsLocationInformationCollector())
        collector.registerCollector(NetworkInformationCollector())

        return collector
    }

    func registerCollector(_ collector: InformationCollector) {
        collectors.append(collector)
    }

    /*func removeCollector(_ collector: InformationCollector) {
        
    }*/

    private let timeBasedResult = TimeBasedResultDto()

    private var timer: Repeater?

    func start(startNs: UInt64) {
        guard timer == nil else {
            return // already started
        }

        ///
        timeBasedResult.cpuUsage = [PointInTimeValueDto<Double>]()
        timeBasedResult.memUsage = [PointInTimeValueDto<Double>]()
        timeBasedResult.geoLocations = [GeoLocationDto]()
        ///

        logger.debug("STARTING SYSTEM INFO COLLECTOR TIMER")

        DispatchQueue.global(qos: .background).async {
            self.collectors.forEach { $0.start(self.timeBasedResult, startNs: startNs) }

            self.timer = Repeater.every(.seconds(1)) { _ in
                self.collectors.forEach { $0.collect(into: self.timeBasedResult) }
            }
        }
    }

    func stop() {
        logger.debug("STOPPING SYSTEM INFO COLLECTOR TIMER")

        DispatchQueue.global(qos: .background).async {
            self.timer?.removeAllObservers(thenStop: true)
            self.timer = nil

            self.collectors.forEach { $0.stop() }
        }
    }

    func getResult() -> TimeBasedResultDto {
        return timeBasedResult
    }
}
