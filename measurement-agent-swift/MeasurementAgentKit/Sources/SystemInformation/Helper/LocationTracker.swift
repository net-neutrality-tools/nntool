// MeasurementAgentKit: LocationTracker.swift, created on 09.07.19
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
public class LocationTracker: NSObject {

    public typealias UpdateLocationCallback = (CLLocation) -> Void

    private var locationManager: CLLocationManager?
    private var updateLocationCallback: UpdateLocationCallback?

    public func start(updateLocationCallback: UpdateLocationCallback? = nil, desiredAccuracy: CLLocationAccuracy = kCLLocationAccuracyBest, distanceFilter: CLLocationDistance = 3.0) {
        guard locationManager == nil else {
            return // already started
        }

        self.updateLocationCallback = updateLocationCallback

        DispatchQueue.main.async {
            self.locationManager = CLLocationManager()
            self.locationManager?.delegate = self

            if CLLocationManager.authorizationStatus() == .notDetermined {
                self.locationManager?.requestWhenInUseAuthorization()
            }

            self.locationManager?.desiredAccuracy = desiredAccuracy
            self.locationManager?.distanceFilter = distanceFilter

            //self.locationManager?.allowsBackgroundLocationUpdates = true
            //self.locationManager?.pausesLocationUpdatesAutomatically = false
        }
    }

    public func stop() {
        DispatchQueue.main.async {
            self.locationManager?.stopUpdatingLocation()
            self.locationManager = nil
        }
    }
}

///
extension LocationTracker: CLLocationManagerDelegate {

    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            DispatchQueue.main.async {
                self.locationManager?.startUpdatingLocation()
                //self.locationManager?.startUpdatingHeading()
            }
        }
    }

    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let lastLocation = locations.last, CLLocationCoordinate2DIsValid(lastLocation.coordinate) else {
            return
        }

        updateLocationCallback?(lastLocation)
    }

    /*public func locationManager(_ manager: CLLocationManager, didUpdateHeading newHeading: CLHeading) {
     
     }*/
}
