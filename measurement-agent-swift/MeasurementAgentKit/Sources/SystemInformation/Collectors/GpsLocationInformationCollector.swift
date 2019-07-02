// MeasurementAgentKit: GpsLocationInformationCollector.swift, created on 13.06.19
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
import nntool_shared_swift

class GpsLocationInformationCollector: NSObject, InformationCollector {

    private var locationManager: CLLocationManager?

    private var latestReportedLocation: CLLocation?

    private var timeBasedResult: TimeBasedResultDto?
    private var startNs: UInt64?

    func start(_ timeBasedResult: TimeBasedResultDto, startNs: UInt64) {
        self.timeBasedResult = timeBasedResult
        self.startNs = startNs

        DispatchQueue.main.async {
            self.locationManager = CLLocationManager()
            self.locationManager?.delegate = self

            if CLLocationManager.authorizationStatus() == .notDetermined {
                self.locationManager?.requestWhenInUseAuthorization()
            }

            self.locationManager?.distanceFilter = 3.0
            self.locationManager?.desiredAccuracy = kCLLocationAccuracyBest

            //self.locationManager?.allowsBackgroundLocationUpdates = true
            //self.locationManager?.pausesLocationUpdatesAutomatically = false
        }
    }

    func stop() {
        DispatchQueue.main.async {
            self.locationManager?.stopUpdatingLocation()
            self.locationManager = nil
        }
    }

    // GPS location collector should always collect if there's an update and not be triggered by 1 second interval!
    func collect(into timeBasedResult: TimeBasedResultDto) {

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

extension GpsLocationInformationCollector: CLLocationManagerDelegate {

    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            DispatchQueue.main.async {
                self.locationManager?.startUpdatingLocation()
                //self.locationManager?.startUpdatingHeading()
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        DispatchQueue.global(qos: .background).async {
            guard let lastLocation = locations.last, CLLocationCoordinate2DIsValid(lastLocation.coordinate) else {
                return
            }

            // don't add same location twice
            if self.locationAlreadyStored(lastLocation) {
                return
            }

            let geoLocation = GeoLocationDto()

            geoLocation.accuracy = lastLocation.horizontalAccuracy
            //lastLocation.verticalAccuracy?
            geoLocation.altitude = lastLocation.altitude
            geoLocation.heading = lastLocation.course
            geoLocation.latitude = lastLocation.coordinate.latitude
            geoLocation.longitude = lastLocation.coordinate.longitude
            geoLocation.provider = "GPS"
            geoLocation.speed = lastLocation.speed

            geoLocation.time = lastLocation.timestamp
            if let ns = self.startNs {
                geoLocation.relativeTimeNs = TimeHelper.currentTimeNs() - ns
            }

            logger.debug("GEOLOCATION: \(geoLocation.debugDescription)")
            
            self.latestReportedLocation = lastLocation
            self.timeBasedResult?.geoLocations?.append(geoLocation)
        }
    }

    /*func locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
     
    }*/
}
