// ios-app: MapViewController.swift, created on 19.03.19
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
import UIKit
import GoogleMaps
import CoreLocation
import MeasurementAgentKit

class MapViewController: UIViewController {

    private var mapView: GMSMapView! // !

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()
    }

    override func viewWillAppear(_ animated: Bool) {
        if mapView == nil {
            initializeMapView()
        }
    }

    //var x: NSKeyValueObservation!

    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey: Any]?, context: UnsafeMutableRawPointer?) {

        guard change?[.oldKey] == nil else {
            return // Only observe the first change
        }

        guard let location = change?[.newKey] as? CLLocation else {
            return
        }

        mapView.animate(with: GMSCameraUpdate.setTarget(location.coordinate, zoom: 10))
        mapView.removeObserver(self, forKeyPath: "myLocation")
    }

    private func initializeMapView() {
        var initialLocation = CLLocationCoordinate2D(latitude: 47.493910, longitude: 11.644471) // TODO: config

        let camera = GMSCameraPosition.camera(withLatitude: initialLocation.latitude, longitude: initialLocation.longitude, zoom: 4) // TODO

        mapView = GMSMapView.map(withFrame: view.bounds, camera: camera)

        // Observe GMSMapView's "myLocation" property to move the camera to the current location.
        mapView.addObserver(self, forKeyPath: "myLocation", options: .new, context: nil)

        //mapView.mapType = .normal // .hybrid, .satellite

        mapView.isMyLocationEnabled = true

        mapView.settings.myLocationButton = true
        mapView.settings.compassButton = true
        mapView.settings.rotateGestures = true
        mapView.settings.tiltGestures = false
        mapView.settings.consumesGesturesInView = true //false

        mapView.delegate = self

        view.addSubview(mapView)

        ///

        let heatmapLayer = createLayer { (x, y, zoom) -> URL? in
            URL(string: "http://localhost:8084/api/v0/tiles/heatmap/\(zoom)/\(x)/\(y).png")
            //URL(string: "http://localhost:8084/api/v1/tiles/heatmaps/default/\(zoom)/\(x)/\(y).png") // TODO: parameters
        }

        let pointLayer = createLayer(zIndex: 101) { (x, y, zoom) -> URL? in
            URL(string: "http://localhost:8084/api/v0/tiles/points/\(zoom)/\(x)/\(y).png")
        }

        heatmapLayer.map = mapView
        pointLayer.map = mapView
    }

    private func createLayer(zIndex: Int32 = 100, tileSize: Int = 256, _ urlConstructor: @escaping GMSTileURLConstructor) -> GMSURLTileLayer {
        let layer = GMSURLTileLayer(urlConstructor: urlConstructor)

        layer.tileSize = tileSize // TODO: 512 for retina displays
        layer.zIndex = zIndex

        layer.clearTileCache()

        return layer
    }
}

extension MapViewController: GMSMapViewDelegate {

}
