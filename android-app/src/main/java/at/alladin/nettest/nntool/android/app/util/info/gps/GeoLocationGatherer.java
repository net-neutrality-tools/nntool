package at.alladin.nettest.nntool.android.app.util.info.gps;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.PermissionUtil;
import at.alladin.nettest.nntool.android.app.util.info.Gatherer;
import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class GeoLocationGatherer extends ListenableGatherer<GeoLocationChangeEvent, GeoLocationChangeListener> {

    private final static String TAG = GeoLocationGatherer.class.getSimpleName();

    /**
     * min time difference for location update
     */
    public static final int GEO_LOC_UPDATE_MIN_TIME = 1000;

    /**
     * min distance difference for location update
     */
    public static final int GEO_LOC_UPDATE_MIN_DISTANCE = 0;

    private LocationListenerImpl locationListener;

    @Override
    public void addListener(GeoLocationChangeListener listener) {
        super.addListener(listener);
        if (listener != null) {
            listener.onLocationChanged(getCurrentValue());
        }
    }

    @Override
    public void onStart() {
        final LocationManager locationManager = getInformationProvider().getLocationManager();

        if (locationManager == null) {
            Log.w(TAG, "LocationManager not available.");
            return;
        }

        if (PermissionUtil.isLocationPermissionGranted(getInformationProvider().getContext())) {
            final Criteria criteriaFine = new Criteria();
            /* "Fine" accuracy means "use GPS". */
            criteriaFine.setAccuracy(Criteria.ACCURACY_FINE);
            criteriaFine.setPowerRequirement(Criteria.POWER_HIGH);
            final String fineProviderName = locationManager.getBestProvider(criteriaFine, true);

            if (fineProviderName != null) {
                onLocationChanged(locationManager.getLastKnownLocation(fineProviderName));

                locationListener = new LocationListenerImpl();
                locationManager.requestLocationUpdates(fineProviderName, GEO_LOC_UPDATE_MIN_TIME,
                        GEO_LOC_UPDATE_MIN_DISTANCE, locationListener);
            }
        }
        else {
            Log.i(TAG, "Location permissions missing: Manifest.permission.ACCESS_FINE_LOCATION and/or Manifest.permission.ACCESS_COARSE_LOCATION");
        }
    }

    @Override
    public void onStop() {
        final LocationManager locationManager = getInformationProvider().getLocationManager();

        if (locationListener != null && locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    protected void onLocationChanged(final Location location) {

    }

    private class LocationListenerImpl implements LocationListener
    {

        @Override
        public void onLocationChanged(final Location location)
        {
            final String outString = "Location: " + String.valueOf(location.getLatitude()) + "/"
                    + String.valueOf(location.getLongitude()) + " +/-" + String.valueOf(location.getAccuracy())
                    + "m provider: " + String.valueOf(location.getProvider());
            Log.d(TAG, outString);
            GeoLocationGatherer.this.onLocationChanged(location);
        }

        @Override
        public void onProviderDisabled(final String provider)
        {
            Log.d(TAG, "provider disabled: " + provider);
        }

        @Override
        public void onProviderEnabled(final String provider)
        {
            Log.d(TAG, "provider enabled: " + provider);
        }

        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras)
        {
            Log.d(TAG, "status changed: " + provider + "=" + status);
        }
    }


}
