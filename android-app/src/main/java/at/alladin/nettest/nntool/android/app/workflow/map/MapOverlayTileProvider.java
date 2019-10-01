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

import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class MapOverlayTileProvider extends UrlTileProvider {

    private static final String TAG = "MapOverlayTileProvider";

    private static final int tilesize = 256;

    private final MapOverlayType type;

    private String mapServiceUrl;

    public MapOverlayTileProvider (final MapOverlayType type, final Context context) {
        super (tilesize, tilesize);
        mapServiceUrl = PreferencesUtil.getMapServiceUrl(context);
        this.type = type;
    }

    @Override
    public URL getTileUrl(int x, int y, int zoom) {
        try {
            final URI uri = new URI("http", null, mapServiceUrl, 8084, String.format("/api/v0/tiles/%s/", type.pathSuffix), String.format(Locale.US, "%spath=%d/%d/%d&point_diameter=%d&size=%d",
                    "", zoom, x, y, 12, tilesize), null);
            Log.d(TAG, uri.toString());
            return uri.toURL();
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
