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
