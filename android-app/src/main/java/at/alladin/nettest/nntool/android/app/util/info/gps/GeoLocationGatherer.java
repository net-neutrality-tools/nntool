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

package at.alladin.nettest.nntool.android.app.util.info.gps;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.util.PermissionUtil;
import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;

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

    /**
     *
     */
    public static final int GEO_ACCEPT_TIME = 1000 * 60 * 15;

    /**
     *
     */
    public static final int GEO_MIN_TIME = 1000 * 60;

    /**
     *
     */
    private LocationListenerImpl locationListener;

    private AtomicBoolean isLocationEnabled = new AtomicBoolean(false);

    @Override
    public void addListener(GeoLocationChangeListener listener) {
        super.addListener(listener);
        if (listener != null) {
            listener.onLocationChanged(getCurrentValue());
        }
    }

    @Override
    public void onStart() {
        final LocationManager locationManager = getLocationManager();

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

    protected void onLocationEnabled() {
        if (!isLocationEnabled.getAndSet(true)) {
            Log.d(TAG, "Location provider(s) enabled.");
            final GeoLocationChangeEvent event = new GeoLocationChangeEvent(null,
                    GeoLocationChangeEvent.GeoLocationChangeEventType.ENABLED);
            emitGeoLocationChangeEvent(event);
        }
    }

    protected void onLocationDisabled() {
        if (isLocationEnabled.getAndSet(false)) {
            Log.d(TAG, "All location providers disabled.");
            final GeoLocationChangeEvent event = new GeoLocationChangeEvent(null,
                    GeoLocationChangeEvent.GeoLocationChangeEventType.DISABLED);
            emitGeoLocationChangeEvent(event);
        }
    }

    protected void onLocationChanged(final Location location) {
        isLocationEnabled.set(true);
        if (!isBetterLocation(getCurrentValue(), location)) {
            return;
        }

        final GeoLocationChangeEvent event = new GeoLocationChangeEvent(toGeoLocationDto(location),
                GeoLocationChangeEvent.GeoLocationChangeEventType.LOCATION_UPDATE);
        emitGeoLocationChangeEvent(event);
    }

    private void emitGeoLocationChangeEvent(final GeoLocationChangeEvent event) {
        setCurrentValue(event);
        for (final GeoLocationChangeListener listener : getListenerList()) {
            listener.onLocationChanged(event);
        }
    }

    public boolean isLocationEnabled() {
        return isLocationEnabled.get();
    }

    private static GeoLocationDto toGeoLocationDto(final Location location) {
        if (location == null) {
            return null;
        }

        final GeoLocationDto dto = new GeoLocationDto();
        dto.setAccuracy((double)location.getAccuracy());
        dto.setAltitude(location.getAltitude());
        dto.setHeading((double)location.getBearing());
        dto.setLatitude(location.getLatitude());
        dto.setLongitude(location.getLongitude());
        dto.setProvider(location.getProvider());
        dto.setSpeed((double) location.getSpeed());
        dto.setTime(new LocalDateTime(location.getTime(), DateTimeZone.UTC));
        return dto;
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
            final LocationManager lm = GeoLocationGatherer.this.getLocationManager();
            if (lm != null) {
                boolean isAnyLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isAnyLocationEnabled) {
                    GeoLocationGatherer.this.onLocationDisabled();
                }
            }
        }

        @Override
        public void onProviderEnabled(final String provider)
        {
            Log.d(TAG, "provider enabled: " + provider);
            final LocationManager lm = GeoLocationGatherer.this.getLocationManager();
            if (lm != null) {
                boolean isAnyLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (isAnyLocationEnabled) {
                    GeoLocationGatherer.this.onLocationEnabled();
                }
            }
        }

        @Override
        public void onStatusChanged(final String provider, final int status, final Bundle extras)
        {
            Log.d(TAG, "status changed: " + provider + "=" + status);
        }
    }

    private static boolean isBetterLocation(final GeoLocationChangeEvent curLocation, final Location newLocation) {
        return isBetterLocation(curLocation != null ? curLocation.getGeoLocationDto() : null,
                newLocation != null ? toGeoLocationDto(newLocation) : null);
    }

    private static boolean isBetterLocation(final GeoLocationDto curLocation, final GeoLocationDto newLocation)
    {
        if (newLocation == null) {
            return false;
        }

        final long locTime = newLocation.getTime().toDateTime().getMillis(); //milliseconds since January 1, 1970

        // discard locations older than Config.GEO_ACCEPT_TIME milliseconds
        // System.nanoTime() and newLocation.getElapsedRealtimeNanos() would be
        // more accurate but would require API level 17
        final long now = System.currentTimeMillis();
        Log.d(TAG, "age: " + (now - locTime) + " ms");
        if (now > locTime + GEO_ACCEPT_TIME) {
            return false;
        }

        if (curLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        final long timeDelta = locTime - curLocation.getTime().toDateTime().getMillis();
        final boolean isSignificantlyNewer = timeDelta > GEO_MIN_TIME;
        final boolean isSignificantlyOlder = timeDelta < -GEO_MIN_TIME;
        final boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location,
        // use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            // If the new location is more than two minutes older, it must
            // be worse
            return true;
        }
        else if (isSignificantlyOlder) {
            // keep old value
            return false;
        }
        else {
            // Check whether the new location fix is more or less accurate
            final int accuracyDelta = (int) (newLocation.getAccuracy() - curLocation.getAccuracy());
            final boolean isLessAccurate = accuracyDelta > 0;
            final boolean isMoreAccurate = accuracyDelta < 0;
            final boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            final boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), curLocation.getProvider());

            final boolean isSamePosition = newLocation.getLatitude().equals(curLocation.getLatitude())
                    && newLocation.getLongitude().equals(curLocation.getLongitude());

            // Determine location quality using a combination of timeliness
            // and accuracy
            if (isMoreAccurate) {
                return true;
            }
            else if (isNewer && !isLessAccurate && !isSamePosition) {
                return true;
            }
            else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider &&!isSamePosition) {
                return true;
            }
            // keep old location otherwise
        }

        return false;
    }

    /** Checks whether two providers are the same */
    private static boolean isSameProvider(final String provider1, final String provider2)
    {
        if (provider1 == null)
            return provider2 == null;
        return provider1.equals(provider2);
    }

}
