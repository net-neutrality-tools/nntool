package at.alladin.nettest.nntool.android.app.util.info.gps;

import at.alladin.nettest.nntool.android.app.util.info.InformationServiceListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface GeoLocationChangeListener extends InformationServiceListener {

    void onLocationChanged(final GeoLocationChangeEvent geoLocationChangeEvent);
}
