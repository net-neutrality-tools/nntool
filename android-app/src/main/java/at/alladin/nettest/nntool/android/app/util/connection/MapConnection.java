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

package at.alladin.nettest.nntool.android.app.util.connection;

import android.util.Log;

import java.util.Map;

import at.alladin.nntool.shared.map.MapCoordinate;
import at.alladin.nntool.shared.map.MapMarkerRequest;
import at.alladin.nntool.shared.map.MapMarkerResponse;
import at.alladin.nntool.shared.map.info.MapInfoResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class MapConnection extends AbstractConnection<MapService> {

    public MapConnection(final String url) {
        super(url, null, MapService.class);
    }

    public MapConnection(final boolean isEncrypted, final String hostname, final int port, final String pathPrefix) {
        super(isEncrypted, hostname, null, port, pathPrefix, MapService.class);
    }

    public MapMarkerResponse requestMapMarker(final MapCoordinate coordinate, final Map<String, String> mapOptions, final Map<String, String> mapFilters) {

        final MapMarkerRequest request = new MapMarkerRequest();
        request.setCoordinates(coordinate);

        try {
            return getControllerService().getMeasurementsRequest(request).execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public MapInfoResponse requestMapInfo() {
        try {
            return getControllerService().getMapInfoResponse().execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
