package at.alladin.nettest.nntool.android.app.util.info;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

import static at.alladin.nettest.nntool.android.app.util.info.NetworkTypeAware.NETWORK_WIFI;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalGatherer extends Gatherer<Integer> {

    private final static String TAG = SignalGatherer.class.getSimpleName();

    private final NetworkStateBroadcastReceiver networkStateBroadcastReceiver = new NetworkStateBroadcastReceiver();

    private final TelephonyStateListener telephonyStateListener = new TelephonyStateListener();

    @Override
    public void start() {
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        getInformationProvider().getContext().registerReceiver(networkStateBroadcastReceiver, intentFilter);

        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
        getTelephonyManager().listen(telephonyStateListener, events);
    }

    @Override
    public void stop() {
        getInformationProvider().getContext().unregisterReceiver(networkStateBroadcastReceiver);
        getTelephonyManager().listen(telephonyStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private int getNetwork() {
        final NetworkGatherer ng = getInformationProvider().getGatherer(NetworkGatherer.class);
        if (ng != null) {
            return ng.getNetwork();
        }

        return Integer.MAX_VALUE;
    }

    private class NetworkStateBroadcastReceiver extends BroadcastReceiver {
        private final Integer ACCEPT_WIFI_RSSI_MIN = -113;

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(WifiManager.RSSI_CHANGED_ACTION) && getNetwork() == NETWORK_WIFI) {
                final WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
                final int rssi = wifiInfo.getRssi();
                if (rssi != -1 && rssi >= ACCEPT_WIFI_RSSI_MIN)  {
                    Log.i(TAG, "wifi link speed: " + wifiInfo.getLinkSpeed() + ", rssi: " + rssi);
                }
            }
        }
    }

    public class TelephonyStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            final int networkType = getNetwork();
            if (networkType != NetworkTypeAware.NETWORK_ETHERNET
                    && networkType != NetworkTypeAware.NETWORK_WIFI
                    && networkType != NetworkTypeAware.NETWORK_BLUETOOTH) {
                Log.i(TAG, "mobile signal: " + signalStrength);
                Log.i(TAG, "mobile signal clazz: " + signalStrength.getClass());
            }
        }
    }
}
