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
import Siesta

public enum TileLayerType: String {
    case heatmap
    case points
}

///
public class MapService: RestApiService {

    init(baseURL: URLConvertible, agent: MeasurementAgent) {
        super.init(baseURL: baseURL, agent: agent)

        configureTransformer("/tiles/markers", forType: MapMarkerResponse.self)
    }

    // TODO: parameters
    public func tileLayerUrl(_ type: TileLayerType, x: UInt, y: UInt, zoom: UInt) -> URL? {
        var tileURL = service.baseURL?.appendingPathComponent("tiles")

        tileURL = tileURL?.appendingPathComponent(type.rawValue)

        tileURL = tileURL?
                    .appendingPathComponent("\(zoom)")
                    .appendingPathComponent("\(x)")
                    .appendingPathComponent("\(y)")

        tileURL = tileURL?.appendingPathExtension("png")

        return tileURL
    }

    public func getMarkers(mapMarkerRequest: MapMarkerRequest, onSuccess: SuccessCallback<MapMarkerResponse>?, onFailure: FailureCallback?) {
        request("/tiles/markers", method: .post, requestEntity: mapMarkerRequest, wrapInApiRequest: false, responseEntityType: MapMarkerResponse.self, onSuccess: onSuccess, onFailure: onFailure)
    }
}
