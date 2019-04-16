package at.alladin.nettest.nntool.android.app.util.info;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class NetworkGatherer extends Gatherer<NetworkGatherer.NetworkChangeEvent> implements NetworkTypeAware {

    private final static String TAG = NetworkGatherer.class.getSimpleName();

    private final AtomicInteger lastNetworkType = new AtomicInteger(Integer.MAX_VALUE);

    private final NetworkStateBroadcastReceiver networkStateBroadcastReceiver = new NetworkStateBroadcastReceiver();

    private final TelephonyStateListener telephonyStateListener = new TelephonyStateListener();

    private List<NetworkChangeListener> networkChangeListenerList = new ArrayList<>();

    public interface OperatorInfo {
        String getOperatorName();
    }

    public final static class WifiOperator implements OperatorInfo {
        final String ssid;

        final String bssid;

        final int networkId;

        public WifiOperator(final WifiInfo wifiInfo) {
            this.ssid = removeQuotationsInCurrentSSIDForJellyBean(wifiInfo.getSSID());
            this.bssid = wifiInfo.getBSSID();
            this.networkId = wifiInfo.getNetworkId();
        }

        public String getSsid() {
            return ssid;
        }

        public String getBssid() {
            return bssid;
        }

        public int getNetworkId() {
            return networkId;
        }

        @Override
        public String getOperatorName() {
            return ssid;
        }

        @Override
        public String toString() {
            return "WifiOperator{" +
                    "ssid='" + ssid + '\'' +
                    ", bssid='" + bssid + '\'' +
                    ", networkId='" + networkId + '\'' +
                    '}';
        }
    }

    public final static class MobileOperator implements OperatorInfo {

        private final String networkOperatorName;
        private final String networkOperator;
        private final String networkCountryCode;
        private final String simOperator;
        private final String simOpetatorName;
        private final String simCountryCode;

        public MobileOperator(final TelephonyManager telephonyManager) {
            final String tNetworkOperator = telephonyManager.getNetworkOperator();
            if (tNetworkOperator != null && tNetworkOperator.length() >= 5) {
                this.networkOperator = String.format("%s-%s", tNetworkOperator.substring(0, 3), tNetworkOperator.substring(3));
            }
            else {
                this.networkOperator = "";
            }

            this.networkOperatorName = telephonyManager.getNetworkOperatorName();
            this.networkCountryCode = telephonyManager.getNetworkCountryIso();

            final String tSimOperator = telephonyManager.getSimOperator();
            if (tSimOperator != null && tSimOperator.length() >= 5) {
                this.simOperator = String.format("%s-%s", tSimOperator.substring(0, 3), tSimOperator.substring(3));
            }
            else {
                this.simOperator = "";
            }
            this.simOpetatorName = telephonyManager.getSimOperatorName();
            this.simCountryCode = telephonyManager.getSimCountryIso();
        }

        /**
         * Returns the alphabetic name of current registered operator.
         * @return
         */
        public String getNetworkOperatorName() {
            return networkOperatorName;
        }

        /**
         * Returns the numeric name (MCC+MNC) of current registered operator.
         * @return
         */
        public String getNetworkOperator() {
            return networkOperator;
        }

        /**
         * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.
         * @return
         */
        public String getSimOperator() {
            return simOperator;
        }

        /**
         * Returns the Service Provider Name (SPN).
         * @return
         */
        public String getSimOpetatorName() {
            return simOpetatorName;
        }

        /**
         * Returns the ISO country code equivalent for the SIM provider's country code.
         * @return
         */
        public String getSimCountryCode() {
            return simCountryCode;
        }

        /**
         * Returns the ISO country code equivalent of the MCC (Mobile Country Code) of the current registered operator or the cell nearby, if available.
         * @return
         */
        public String getNetworkCountryCode() {
            return networkCountryCode;
        }

        @Override
        public String getOperatorName() {
            if (networkOperator.length() == 0 && networkOperatorName.length() == 0) {
                return "-";
            }
            else if (networkOperator.length() == 0) {
                return networkOperatorName;
            }
            else if (networkOperatorName.length() == 0) {
                return networkOperator;
            }

            return String.format("%s (%s)", networkOperatorName, networkOperator);
        }

        @Override
        public String toString() {
            return "MobileOperator{" +
                    "networkOperatorName='" + networkOperatorName + '\'' +
                    ", networkOperator='" + networkOperator + '\'' +
                    ", networkCountryCode='" + networkCountryCode + '\'' +
                    ", simOperator='" + simOperator + '\'' +
                    ", simOpetatorName='" + simOpetatorName + '\'' +
                    ", simCountryCode='" + simCountryCode + '\'' +
                    '}';
        }
    }

    public final static class NetworkChangeEvent {

        /**
         * System.nanoTime()
         */
        private long timestampNs;

        private Integer networkType;

        private WifiOperator wifiOperator;

        private MobileOperator mobileOperator;

        public NetworkChangeEvent(final Integer networkType) {
            this.networkType = networkType;
            this.timestampNs = System.nanoTime();
        }

        public WifiOperator getWifiOperator() {
            return wifiOperator;
        }

        public void setWifiOperator(WifiOperator wifiOperator) {
            this.wifiOperator = wifiOperator;
        }

        public MobileOperator getMobileOperator() {
            return mobileOperator;
        }

        public void setMobileOperator(MobileOperator mobileOperator) {
            this.mobileOperator = mobileOperator;
        }

        public long getTimestampNs() {
            return timestampNs;
        }

        public void setTimestampNs(long timestampNs) {
            this.timestampNs = timestampNs;
        }

        public Integer getNetworkType() {
            return networkType;
        }

        public void setNetworkType(Integer networkType) {
            this.networkType = networkType;
        }

        @Override
        public String toString() {
            return "NetworkChangeEvent{" +
                    "timestampNs=" + timestampNs +
                    ", networkType=" + networkType +
                    ", wifiOperator=" + wifiOperator +
                    ", mobileOperator=" + mobileOperator +
                    '}';
        }
    }

    public interface NetworkChangeListener {
        void onNetworkChange(NetworkChangeEvent event);
    }

    public static String removeQuotationsInCurrentSSIDForJellyBean(String ssid)
    {
        if (Build.VERSION.SDK_INT >= 17 && ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    public void addNetworkChangeListener(final NetworkChangeListener listener) {
        if (listener != null && !networkChangeListenerList.contains(listener)) {
            networkChangeListenerList.add(listener);
            listener.onNetworkChange(getCurrentValue());
        }
    }

    public boolean removeNetworkCHangeListener(final NetworkChangeListener listener) {
        return networkChangeListenerList.remove(listener);
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
    public void start() {
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
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
    }

    @Override
    public void stop() {
        getInformationProvider().getContext().unregisterReceiver(networkStateBroadcastReceiver);
        getTelephonyManager().listen(telephonyStateListener, PhoneStateListener.LISTEN_NONE);
        /*
        getConnectivityManager().unregisterNetworkCallback(networkCallback);
        */
    }

    @SuppressWarnings("deprecation")
    private int getNetworkTypePreApi23() {
        int result = TelephonyManager.NETWORK_TYPE_UNKNOWN;

        final NetworkInfo activeNetworkInfo = getConnectivityManager().getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            final int type = activeNetworkInfo.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    result = NetworkTypeAware.NETWORK_WIFI;
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

    @TargetApi(Build.VERSION_CODES.M)
    private int getNetworkTypeApi23() {
        int result = TelephonyManager.NETWORK_TYPE_UNKNOWN;

        final Network activeNetwork = getConnectivityManager().getActiveNetwork();
        if (activeNetwork != null) {
            final NetworkCapabilities networkCapabilities = getConnectivityManager().getNetworkCapabilities(activeNetwork);
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = NetworkTypeAware.NETWORK_WIFI;
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
        if (event == null || lastNetworkType.get() != result) {
            final NetworkChangeEvent networkChangeEvent = new NetworkChangeEvent(result);
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

            if (networkChangeListenerList != null) {
                for (final NetworkChangeListener listener : networkChangeListenerList) {
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
}
