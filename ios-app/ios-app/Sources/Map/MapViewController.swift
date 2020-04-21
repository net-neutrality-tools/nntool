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
import UIKit
import GoogleMaps
import CoreLocation
import MeasurementAgentKit

class MapViewController: UIViewController {

    private var mapView: GMSMapView! // !

    private var heatmapLayer: GMSURLTileLayer! // !
    private var pointLayer: GMSURLTileLayer! // !

    private lazy var mapService: MapService? = {
        return MEASUREMENT_AGENT.mapService
    }()

    var currentMarker: GMSMarker?

    private lazy var emptyMarkerIcon: UIImage! = {
        UIGraphicsBeginImageContextWithOptions(CGSize(width: 1, height: 1), false, 0.0)
        let icon = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return icon
    }()

    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.applyIconFontAttributes()
    }

    override func viewWillAppear(_ animated: Bool) {
        if mapView == nil {
            initializeMapView()
        } else {
            refresh()
        }
    }

    private func refresh() {
        heatmapLayer.clearTileCache()
        pointLayer.clearTileCache()
    }

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
        // Google Map View throws an NSException if the API key is not provided.
        // To fix crashes of the app, we check if a Google Map API key is set prior to
        // initializing the map view (The Google Maps API key is provided in AppDelegate).
        guard GOOGLE_MAPS_API_KEY != "" else {
            return
        }

        let camera = GMSCameraPosition.camera(
            withLatitude: MAP_VIEW_INITIAL_LOCATION_LATITUDE,
            longitude: MAP_VIEW_INITIAL_LOCATION_LONGITUDE,
            zoom: Float(MAP_VIEW_INITIAL_ZOOM)
        )

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

        heatmapLayer = createLayer { (x, y, zoom) -> URL? in
            self.mapService?.tileLayerUrl(.heatmap, x: x, y: y, zoom: zoom) // TODO: parameters
        }

        pointLayer = createLayer(zIndex: 101) { (x, y, zoom) -> URL? in
            self.mapService?.tileLayerUrl(.points, x: x, y: y, zoom: zoom) // TODO: parameters
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

    func mapView(_ mapView: GMSMapView, idleAt position: GMSCameraPosition) {
        if position.zoom >= Float(MAP_VIEW_POINT_LAYER_ZOOM_THRESHOLD) {
            pointLayer.map = mapView
        } else {
            pointLayer.map = nil
        }
    }

    func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        currentMarker?.map = nil
        mapView.selectedMarker = nil
        currentMarker = nil

        guard pointLayer.map != nil else {
            // We are not showing points -> do nothing on tap
            return
        }

        let coord = MapCoordinate()
        coord.latitude = coordinate.latitude
        coord.longitude = coordinate.longitude
        coord.zoom = UInt(mapView.camera.zoom)

        let mapMarkerRequest = MapMarkerRequest()
        mapMarkerRequest.coordinates = coord

        mapService?.getMarkers(mapMarkerRequest: mapMarkerRequest, onSuccess: { response in
            guard let marker = response.markers?.first else {
                return
            }

            self.currentMarker = GMSMarker(position: coordinate)
            self.currentMarker?.icon = self.emptyMarkerIcon
            self.currentMarker?.userData = marker
            self.currentMarker?.appearAnimation = .pop
            self.currentMarker?.map = mapView
            self.currentMarker?.userData = marker

            DispatchQueue.main.async {
                self.mapView.selectedMarker = self.currentMarker
            }
        }, onFailure: { error in
            logger.error(error)
        })
    }

    func mapView(_ mapView: GMSMapView, markerInfoContents marker: GMSMarker) -> UIView? {
        let screenBounds = UIScreen.main.bounds

        var l: UIStackView?

        if let m = marker.userData as? MapMarker, let results = m.results {
            let markerHeight = CGFloat(results.count) * UIFont.smallSystemFontSize + 20

            l = UIStackView(frame: CGRect(x: 0, y: 0, width: screenBounds.width * 0.65, height: markerHeight))
            l?.axis = .horizontal
            l?.alignment = .fill
            l?.distribution = .fillProportionally
            l?.spacing = 5

            let ll = UIStackView()
            ll.axis = .vertical
            ll.alignment = .leading
            ll.distribution = .equalSpacing

            let lr = UIStackView()
            lr.axis = .vertical
            lr.alignment = .leading
            lr.distribution = .equalSpacing

            results.forEach { i in
                let titleLabel = UILabel()
                titleLabel.font = UIFont.systemFont(ofSize: UIFont.smallSystemFontSize)

                if let t = i.title {
                    titleLabel.text = "\(t):"
                }

                ll.addArrangedSubview(titleLabel)

                var val = i.value ?? "-"
                if let unit = i.unit, val != "-" {
                    val += " \(unit)"
                }

                let valueLabel = UILabel()
                valueLabel.font = UIFont.systemFont(ofSize: UIFont.smallSystemFontSize)
                valueLabel.text = val

                lr.addArrangedSubview(valueLabel)
            }

            l?.addArrangedSubview(ll)
            l?.addArrangedSubview(lr)
        }

        return l
    }

    func mapView(_ mapView: GMSMapView, didTapInfoWindowOf marker: GMSMarker) {
        guard let markerData = marker.userData as? MapMarker, var openDataUuid = markerData.openDataUuid else {
            return
        }

        // TODO: remove O on server-side
        if openDataUuid.starts(with: "O") {
            openDataUuid.removeFirst()
        }

        presentWebBrowserWithUrlString("https://nntool.net-neutrality.tools/open-data-results/\(openDataUuid)")
    }
}
