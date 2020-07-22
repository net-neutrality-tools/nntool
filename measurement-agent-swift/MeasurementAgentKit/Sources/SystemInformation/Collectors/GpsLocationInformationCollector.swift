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
import CoreLocation
import nntool_shared_swift

class GpsLocationInformationCollector: BaseInformationCollector {

    private let locationTracker = LocationTracker()

    private var latestReportedLocation: CLLocation?

    /// GPS location collector should only collect if there's an update and not be triggered by 1 second interval!
    override func start(_ timeBasedResult: TimeBasedResultDto, startNs: UInt64) {
        super.start(timeBasedResult, startNs: startNs)

        locationTracker.start(updateLocationCallback: { lastLocation in
            // don't add same location twice
            if self.locationAlreadyStored(lastLocation) {
                return
            }

            let geoLocation = GeoLocationDto()

            geoLocation.accuracy = lastLocation.horizontalAccuracy
            //lastLocation.verticalAccuracy?
            geoLocation.altitude = lastLocation.altitude
            
            if lastLocation.course >= 0 {
                geoLocation.heading = lastLocation.course
            }
            
            geoLocation.latitude = lastLocation.coordinate.latitude
            geoLocation.longitude = lastLocation.coordinate.longitude
            //geoLocation.provider = nil
            geoLocation.speed = lastLocation.speed

            geoLocation.time = TimeHelper.localToUTCDate(localDate: lastLocation.timestamp)
            geoLocation.relativeTimeNs = self.currentRelativeTimeNs()

            logger.debug("GEOLOCATION: \(geoLocation.debugDescription)")

            self.latestReportedLocation = lastLocation
            timeBasedResult.geoLocations?.append(geoLocation)
        })
    }

    override func stop() {
        super.stop()

        locationTracker.stop()
    }

    func locationAlreadyStored(_ location: CLLocation) -> Bool {
        guard let lrl = latestReportedLocation else {
            return false
        }

        return  lrl.coordinate.latitude == location.coordinate.latitude &&
                lrl.coordinate.longitude == location.coordinate.longitude &&
                lrl.altitude == location.altitude &&
                lrl.course == location.course &&
                lrl.horizontalAccuracy == location.horizontalAccuracy &&
                lrl.speed == location.speed
    }
}
