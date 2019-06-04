package at.alladin.nettest.nntool.android.app.util.info;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 *
 * Information Provder is used to manage gatherer (data/information collectors)
 */
public class InformationProvider {

    final ConnectivityManager connectivityManager;

    final TelephonyManager telephonyManager;

    final WifiManager wifiManager;

    final LocationManager locationManager;

    final Context context;

    final Map<Class<?>, Gatherer> gathererMap = new HashMap<>();

    final AtomicBoolean isRunning = new AtomicBoolean(false);

    public InformationProvider(final Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void stop() {
        isRunning.set(false);
        for (final Map.Entry<Class<?>, Gatherer> e : gathererMap.entrySet()) {
            e.getValue().onStop();
        }
    }

    public void start() {
        isRunning.set(true);
        for (final Map.Entry<Class<?>, Gatherer> e : gathererMap.entrySet()) {
            e.getValue().onStart();
        }
    }

    public <T extends Gatherer> T registerGatherer(final Class<T> clazz) {
        try {
            final T gatherer = clazz.newInstance();
            return registerGatherer(gatherer, clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public <T extends Gatherer> T registerGatherer(final T gatherer, final Class<T> clazz) {
        if (gathererMap.containsKey(clazz)) {
            return (T) gathererMap.get(clazz);
        }

        gatherer.setInformationProvider(this);
        gathererMap.put(clazz, gatherer);

        if (isRunning.get()) {
            try {
                gatherer.onStart();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }

        return gatherer;
    }

    public <T extends Gatherer> T unregisterGatherer(final Class<T> gathererClazz) {
        final T gatherer = (T) gathererMap.remove(gathererClazz);
        if (gatherer != null && isRunning.get()) {
            gatherer.onStop();
        }
        return gatherer;
    }

    public <T extends Gatherer> T getGatherer(final Class<T> gathererClazz) {
        return (T) gathererMap.get(gathererClazz);
    }

    public Context getContext() {
        return context;
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public TelephonyManager getTelephonyManager() {
        return telephonyManager;
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }
}
