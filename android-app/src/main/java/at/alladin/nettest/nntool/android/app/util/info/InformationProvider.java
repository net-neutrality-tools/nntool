package at.alladin.nettest.nntool.android.app.util.info;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class InformationProvider {

    final ConnectivityManager connectivityManager;

    final TelephonyManager telephonyManager;

    final WifiManager wifiManager;

    final Context context;

    final Map<Class<?>, Gatherer> gathererMap = new HashMap<>();

    public InformationProvider(final Context context) {
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void onPause() {
        for (final Map.Entry<Class<?>, Gatherer> e : gathererMap.entrySet()) {
            e.getValue().stop();
        }
    }

    public void onResume() {
        for (final Map.Entry<Class<?>, Gatherer> e : gathererMap.entrySet()) {
            e.getValue().start();
        }
    }

    public <T extends Gatherer> boolean registerGatherer(final Class<T> clazz) {
        try {
            final T gatherer = clazz.newInstance();
            return registerGatherer(gatherer, clazz);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return false;
    }

    public <T extends Gatherer> boolean registerGatherer(final T gatherer, final Class<T> clazz) {
        if (gathererMap.containsKey(clazz)) {
            return false;
        }

        gatherer.setInformationProvider(this);
        gathererMap.put(clazz, gatherer);
        return true;
    }

    public <T extends Gatherer> T unregisterGatherer(final Class<T> gathererClazz) {
        return (T) gathererMap.remove(gathererClazz);
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
}
