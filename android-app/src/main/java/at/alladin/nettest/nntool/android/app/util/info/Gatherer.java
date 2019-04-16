package at.alladin.nettest.nntool.android.app.util.info;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class Gatherer<T> {

    InformationProvider informationProvider;

    final AtomicReference<T> currentValue = new AtomicReference<>();

    public InformationProvider getInformationProvider() {
        return informationProvider;
    }

    public void setInformationProvider(final InformationProvider informationProvider) {
        this.informationProvider = informationProvider;
    }

    public T getCurrentValue() {
        return currentValue.get();
    }

    public void setCurrentValue(final T currentValue) {
        this.currentValue.set(currentValue);
    }

    public ConnectivityManager getConnectivityManager() {
        if (informationProvider != null) {
            return informationProvider.getConnectivityManager();
        }
        return null;
    }

    public TelephonyManager getTelephonyManager() {
        if (informationProvider != null) {
            return informationProvider.getTelephonyManager();
        }
        return null;
    }

    public WifiManager getWifiManager() {
        if (informationProvider != null) {
            return informationProvider.getWifiManager();
        }
        return null;
    }

    public abstract void start();

    public abstract void stop();
}
