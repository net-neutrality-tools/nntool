/***************************************************************************
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
 ***************************************************************************/

import Foundation

/// Helper class for timestamps.
final class TimeHelper {

    /// Returns the current nanosecond timstamp based on CPU ticks.
    class func currentTimeNs() -> UInt64 {
        var info = mach_timebase_info(numer: 0, denom: 0)
        mach_timebase_info(&info)

        let ticks = mach_absolute_time()

        return (ticks * UInt64(info.numer)) / UInt64(info.denom)
    }

    /// Returns the current timestamp as milliseconds.
    class func currentTimeMs() -> UInt64 {
        return UInt64(Date().timeIntervalSince1970 * 1000)
    }
}
