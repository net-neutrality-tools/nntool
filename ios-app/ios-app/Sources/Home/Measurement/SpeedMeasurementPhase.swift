// ios-app: SpeedMeasurementPhase.swift, created on 23.04.19
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

enum SpeedMeasurementPhase: String {
    case initialize = "init"
    case rtt        = "rtt"
    case download   = "download"
    case upload     = "upload"

    var icon: IconFont {
        switch self {
        case .initialize: return .hourglass // TODO
        case .rtt:        return .rtt
        case .download:   return .down
        case .upload:     return .up
        }
    }

    var localizedString: String {
        return NSLocalizedString("measurement.speed.phase.\(self.rawValue)", comment: "Translation of speed measurement phase '\(self.rawValue)'")
    }

    var phaseIndex: UInt {
        switch self {
        case .initialize: return 0
        case .rtt:        return 1
        case .download:   return 2
        case .upload:     return 3
        }
    }
}
