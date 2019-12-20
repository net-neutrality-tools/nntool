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

/// Holds a value from a point in time.
class PointInTimeValueDto<T: Codable>: Codable {

    /// Relative time in nanoseconds (to test begin).
    var relativeTimeNs: UInt64?

    /// The value recorded at this point in time.
    var value: T?

    ///
    enum CodingKeys: String, CodingKey {
        case relativeTimeNs = "relative_time_ns"
        case value
    }
}
