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

package at.alladin.nettest.nntool.android.app.workflow.map;

import android.content.Context;

import com.google.android.gms.maps.model.UrlTileProvider;

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
            return new URL(mapServiceUrl + "/tiles/" + type.pathSuffix + String.format(Locale.US, "/?path=%d/%d/%d&point_diameter=%d&size=%d",
                    zoom, x, y, 12, tilesize));
        } catch (Exception ex) {
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
