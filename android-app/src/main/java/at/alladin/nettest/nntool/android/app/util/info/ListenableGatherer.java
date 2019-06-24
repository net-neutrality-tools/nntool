package at.alladin.nettest.nntool.android.app.util.info;

import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class ListenableGatherer<T, L> extends Gatherer<T> {

    protected List<L> listenerList = new ArrayList<>();

    public void addListener(final L listener) {
        if (listener != null && !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public boolean removeListener(final L listener) {
        return listenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return listenerList;
    }
}
