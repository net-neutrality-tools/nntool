package at.alladin.nettest.nntool.android.app.workflow.map;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class MapOverlayTileProvider extends UrlTileProvider {

    private static final String TAG = "MapOverlayTileProvider";

    private static final int tilesize = 256;

    private final MapOverlayType type;

    private final Integer maxZoomForPointMap;

    private String mapServiceUrl;

    public MapOverlayTileProvider (final MapOverlayType type, final Context context) {
        super (tilesize, tilesize);
        mapServiceUrl = PreferencesUtil.getMapServiceUrl(context);
        maxZoomForPointMap = type == MapOverlayType.HEATMAP ? null : context.getResources().getInteger(R.integer.default_map_max_zoom_for_point_map);
        this.type = type;
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        if (maxZoomForPointMap != null && zoom < maxZoomForPointMap) {
            return null;
        }
        try {
            final URI t = new URI(mapServiceUrl);
            final URL test = new URL(mapServiceUrl + "/tiles/" + type.pathSuffix + String.format(Locale.US, "/?path=%d/%d/%d&point_diameter=%d&size=%d",
                    zoom, x, y, 12, tilesize));
            return test;
        } catch (URISyntaxException | MalformedURLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public enum MapOverlayType {
        HEATMAP("heatmap"),
        POINTMAP("points");

        final private String pathSuffix;

        MapOverlayType(final String pathSuffix) {
            this.pathSuffix = pathSuffix;
        }
    }
}
