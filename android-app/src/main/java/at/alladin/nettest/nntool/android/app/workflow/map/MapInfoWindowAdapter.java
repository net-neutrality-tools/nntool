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

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater inflater;

    public MapInfoWindowAdapter (final LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        final View ret = this.inflater.inflate(R.layout.map_info_window, null);

        final TextView titleView = ret.findViewById(R.id.map_info_window_title);
        titleView.setText(marker.getTitle());

        final TextView contentView = ret.findViewById(R.id.map_info_window_content);
        contentView.setText(marker.getSnippet());

        return ret;
    }
}
