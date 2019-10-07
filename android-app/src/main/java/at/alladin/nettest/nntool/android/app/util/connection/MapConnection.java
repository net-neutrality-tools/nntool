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
