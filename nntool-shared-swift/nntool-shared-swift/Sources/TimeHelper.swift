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
public final class TimeHelper {
    
    /// Returns the current nanosecond timstamp based on CPU ticks.
    public class func currentTimeNs() -> UInt64 {
        var info = mach_timebase_info(numer: 0, denom: 0)
        mach_timebase_info(&info)
        
        let ticks = mach_absolute_time()
        
        return (ticks * UInt64(info.numer)) / UInt64(info.denom)
    }
    
    /// Returns the current timestamp as milliseconds.
    public class func currentTimeMs() -> UInt64 {
        return UInt64(Date().timeIntervalSince1970 * 1000)
    }
    /// Returns the current Date in UTC
    public class func currentDateUTC() -> Date {
        var currentTime = Date()
        let timeZoneOffset = TimeZone.current.secondsFromGMT()
        let dateInUTC = Date(timeIntervalSince1970: currentTime.timeIntervalSince1970 - Double(timeZoneOffset))
        return dateInUTC
    }
    
    public class func localToUTCDate(localDate: Date) -> Date {
        let timeZoneOffset = TimeZone.current.secondsFromGMT()
        let dateInUTC = Date(timeIntervalSince1970: localDate.timeIntervalSince1970 - Double(timeZoneOffset))
        return dateInUTC
    }

}
