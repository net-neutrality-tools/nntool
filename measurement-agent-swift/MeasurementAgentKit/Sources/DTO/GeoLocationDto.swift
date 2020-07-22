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

///
public class GeoLocationDto: Codable {

    /// Time and date the geographic location information was captured (UTC).
    public var time: Date?

    /// Geographic location accuracy.
    public var accuracy: Double?

    /// Geographic location altitude.
    public var altitude: Double?

    /// Movement heading.
    public var heading: Double?

    /// Movement speed.
    public var speed: Double?

    /// Geographic location provider.
    public var provider: String?

    /// Geographic location latitude.
    public var latitude: Double?

    /// Geographic location longitude.
    public var longitude: Double?

    /// Relative time in nanoseconds (to measurement begin).
    public var relativeTimeNs: UInt64?

    ///
    enum CodingKeys: String, CodingKey {
        case time
        case accuracy
        case altitude
        case heading
        case speed
        case provider
        case latitude
        case longitude
        case relativeTimeNs = "relative_time_ns"
    }
}
