// ios-app: SpeedHelper.swift, created on 08.05.19
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

class SpeedHelper {

    class func throughputLogarithmMbps(bps: Double, gaugeParts: UInt = 4, maxMbps: Double = 1000) -> Double {
        if bps < 10_000 {
            return 0
        }

        let maxLog = log10(maxMbps)
        let gp = Double(gaugeParts)

        return ((gp - maxLog) + log10(bps / 1e6)) / gp
    }
}
