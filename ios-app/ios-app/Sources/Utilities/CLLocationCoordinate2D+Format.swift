// ios-app: CLLocationCoordinate2D+Format.swift, created on 09.07.19
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
import CoreLocation

///
extension CLLocationCoordinate2D {

    public var dmFormattedString: String {
        let latDirection = latitude >= 0 ? "N" : "S"
        let lonDirection = longitude >= 0 ? "E" : "W"

        let latDegrees = abs(Int(latitude))
        let latMinutes = abs(Double((latitude * 3600).truncatingRemainder(dividingBy: 3600) / 60))

        let lonDegrees = abs(Int(longitude))
        let lonMinutes = abs(Double((longitude * 3600).truncatingRemainder(dividingBy: 3600) / 60))

        return String(
            format: "%d° %.3f' %@ %d° %.3f' %@",
            latDegrees, latMinutes, latDirection,
            lonDegrees, lonMinutes, lonDirection
        )
    }
}