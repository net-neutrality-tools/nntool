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

/// Cell location information from a point in time on the measurement agent.
class CellLocationDto: Codable {

    /// Contains the cell-ID, if available.
    var cellId: Int?

    /// Contains the area code (e.g. location area code (GSM), tracking area code (LTE)), if available.
    var areaCode: Int?

    /// Time and date the signal information was captured (UTC).
    var time: Date?

    /// Contains the primary scrambling code, if available.
    var primaryScramblingCode: Int?

    /// Contains the ARFCN (Absolute Radio Frequency Channel Number) (e.g. 16-bit GSM ARFCN or 18-bit LTE EARFCN), if available.
    var arfcn: Int?

    /// Relative time in nanoseconds (to test begin).
    var relativeTimeNs: UInt64?

    /// Geographic location latitude of this cell.
    var latitude: Double?

    /// Geographic location longitude of this cell.
    var longitude: Double?

    ///
    enum CodingKeys: String, CodingKey {
        case cellId                = "cell_id"
        case areaCode              = "area_code"
        case time
        case primaryScramblingCode = "primary_scrambling_code"
        case arfcn
        case relativeTimeNs        = "relative_time_ns"
        case latitude
        case longitude
    }
}
