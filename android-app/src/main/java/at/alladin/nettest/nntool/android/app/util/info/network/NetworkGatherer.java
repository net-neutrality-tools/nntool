package at.alladin.nettest.nntool.android.app.util.info.network;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalGatherer;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.signal.SignalStrengthChangeListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class NetworkGatherer
        extends ListenableGatherer<NetworkChangeEvent, NetworkChangeListener>
        implements NetworkTypeAware, SignalStrengthChangeListener {

    private final static String TAG = NetworkGatherer.class.getSimpleName();

    private final AtomicInteger lastNetworkType = new AtomicInteger(Integer.MAX_VALUE);

    private final NetworkStateBroadcastReceiver networkStateBroadcastReceiver = new NetworkStateBroadcastReceiver();

    private final TelephonyStateListener telephonyStateListener = new TelephonyStateListener();

    public static String removeQuotationsInCurrentSSIDForJellyBean(String ssid) {
        if (Build.VERSION.SDK_INT >= 17 && ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    @Override
    public void addListener(final NetworkChangeListener listener) {
        super.addListener(listener);
        if (listener != null) {
            listener.onNetworkChange(getCurrentValue());
        }
    }

    /*
    insufficient reliability!

    private ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            Log.d(TAG, "capabilities changed, has internet: " + networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET));
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            Log.d(TAG, "NetworkCallback.onLosing " + network);
        }

        @Override
        public void onLost(Network network) {
            Log.d(TAG, "NetworkCallback.onLost " + network);
        }
    };

    */

    @Override
    public void onStart() {
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getInformationProvider().getContext().registerReceiver(networkStateBroadcastReceiver, intentFilter);

        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
        getTelephonyManager().listen(telephonyStateListener, events);

        /*
        final NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                .build();

        getConnectivityManager().registerNetworkCallback(request, networkCallback);
        */

        final SignalGatherer gatherer = getInformationProvider().getGatherer(SignalGatherer.class);
        if (gatherer != null) {
            gatherer.addListener(this);
        }
    }

    @Override
    public void onStop() {
        getInformationProvider().getContext().unregisterReceiver(networkStateBroadcastReceiver);
        getTelephonyManager().listen(telephonyStateListener, PhoneStateListener.LISTEN_NONE);
        /*
        getConnectivityManager().unregisterNetworkCallback(networkCallback);
        */
        final SignalGatherer gatherer = getInformationProvider().getGatherer(SignalGatherer.class);
        if (gatherer != null) {
            gatherer.removeListener(this);
        }

    }

    @SuppressWarnings("deprecation")
    private int getNetworkTypePreApi23() {
        int result = TelephonyManager.NETWORK_TYPE_UNKNOWN;

        final NetworkInfo activeNetworkInfo = getConnectivityManager().getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            final int type = activeNetworkInfo.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    result = NetworkTypeAware.NETWORK_WLAN;
                    break;

                case ConnectivityManager.TYPE_BLUETOOTH:
                    result = NetworkTypeAware.NETWORK_BLUETOOTH;
                    break;

                case ConnectivityManager.TYPE_ETHERNET:
                    result = NetworkTypeAware.NETWORK_ETHERNET;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                case ConnectivityManager.TYPE_MOBILE_DUN:
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                case ConnectivityManager.TYPE_MOBILE_MMS:
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    result = getTelephonyManager().getNetworkType();
                    break;
            }
        }

        return result;
    }

    private boolean isConnected() {
        final NetworkInfo networkInfo = getConnectivityManager().getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private int getNetworkTypeApi23() {
        int result = TelephonyManager.NETWORK_TYPE_UNKNOWN;

        final Network activeNetwork = getConnectivityManager().getActiveNetwork();
        if (activeNetwork != null) {
            final NetworkCapabilities networkCapabilities = getConnectivityManager().getNetworkCapabilities(activeNetwork);
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = NetworkTypeAware.NETWORK_WLAN;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    result = NetworkTypeAware.NETWORK_ETHERNET;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                    result = NetworkTypeAware.NETWORK_BLUETOOTH;
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = getTelephonyManager().getNetworkType();
                }
            }
        }

        return result;
    }

    @Override
    public int getNetwork() {
        int result = TelephonyManager.NETWORK_TYPE_UNKNOWN;

        if (getConnectivityManager() != null) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                result = getNetworkTypePreApi23();
            }
            else {
                result = getNetworkTypeApi23();
            }
        }

        final NetworkChangeEvent event = getCurrentValue();
        final boolean isConnected = isConnected();

        Log.d(TAG, "new network type: " + result
                + ", old network type: " + lastNetworkType.get() + ", isConnected: " + isConnected);

        boolean emitNewEvent = false;
        final NetworkChangeEvent networkChangeEvent = new NetworkChangeEvent(result);

        if (isConnected && (event == null || lastNetworkType.get() != result)) {
            networkChangeEvent.setEventType(NetworkChangeEvent.NetworkChangeEventType.SIGNAL_UPDATE);
            emitNewEvent = true;
        }
        else if (!isConnected) {
            networkChangeEvent.setEventType(NetworkChangeEvent.NetworkChangeEventType.NO_CONNECTION);
            emitNewEvent = true;
        }

        if (emitNewEvent) {
            if (getWifiManager() != null) {
                final WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
                if (wifiInfo != null) {
                    networkChangeEvent.setWifiOperator(new WifiOperator(wifiInfo));
                }
            }
            if (getTelephonyManager() != null) {
                networkChangeEvent.setMobileOperator(new MobileOperator(getTelephonyManager()));
            }
            setCurrentValue(networkChangeEvent);

            if (getListenerList() != null) {
                for (final NetworkChangeListener listener : getListenerList()) {
                    listener.onNetworkChange(networkChangeEvent);
                }
            }

            lastNetworkType.set(result);
            Log.d(TAG, "new network type: " + getCurrentValue());
        }

        /* detect change from wifi to mobile or reverse */
        /*
        final int lastNetworkType = this.lastNetworkType.get();
        if (result != TelephonyManager.NETWORK_TYPE_UNKNOWN && lastNetworkType != TelephonyManager.NETWORK_TYPE_UNKNOWN)
        {
            if (
                    (result == ConnectivityManager.TYPE_WIFI && lastNetworkType != ConnectivityManager.TYPE_WIFI)
                            ||
                            (result != ConnectivityManager.TYPE_WIFI && lastNetworkType == ConnectivityManager.TYPE_WIFI)
            )
                illegalNetworkTypeChangeDetcted.set(true);
        }
        if (result != lastNetworkType)
        {
            this.lastNetworkType.set(result);
            if (telListener != null)
                telListener.onSignalStrengthsChanged(null);
        }
        */

        return result;
    }

    private class NetworkStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            getNetwork();
        }
    }

    public class TelephonyStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            getNetwork();
        }
    }

    @Override
    public void onSignalStrengthChange(SignalStrengthChangeEvent event) {
        Log.d(TAG, event != null ? event.toString() : "SignalStrengthChangeEvent == null");
        getNetwork();
    }
}
