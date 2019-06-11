package at.alladin.nettest.nntool.android.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.FormatGeoLocationUtil;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.gps.GeoLocationChangeListener;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class GeoLocationView extends RelativeLayout implements GeoLocationChangeListener {

    private final static String TAG = GeoLocationView.class.getSimpleName();

    TextView locationText;
    TextView accuracyText;

    public GeoLocationView(Context context) {
        super(context);
        init();
    }

    public GeoLocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GeoLocationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.geo_location_view, this);
        locationText = findViewById(R.id.text_title_geo_location);
        accuracyText = findViewById(R.id.text_title_geo_location_accuracy);
    }

    public void setLocationText(final String text) {
        if (locationText != null) {
            locationText.setText(text);
        }
    }

    public void setAccuracyText(final String text) {
        if (accuracyText != null) {
            accuracyText.setText(text);
        }
    }

    @Override
    public void onLocationChanged(GeoLocationChangeEvent geoLocationChangeEvent) {
        if (geoLocationChangeEvent != null && geoLocationChangeEvent.getGeoLocationDto() != null) {
            final GeoLocationDto loc = geoLocationChangeEvent.getGeoLocationDto();
            if (loc.getLatitude() != null && loc.getLongitude() != null) {
                Log.d(TAG, "Got new location: " + geoLocationChangeEvent.getGeoLocationDto());
                setLocationText(
                        FormatGeoLocationUtil.formatGeoLat(getContext(), loc.getLatitude()) + " "
                                + FormatGeoLocationUtil.formatGeoLong(getContext(), loc.getLongitude()));

                setAccuracyText(
                        FormatGeoLocationUtil.formatGeoAccuracy(getContext(), loc.getAccuracy(), null)
                );
            }
        }
        else {
            setLocationText("");
            setAccuracyText("");
        }
    }
}
