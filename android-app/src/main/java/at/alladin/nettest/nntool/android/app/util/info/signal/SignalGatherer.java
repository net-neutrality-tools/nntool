package at.alladin.nettest.nntool.android.app.util.info.signal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

import at.alladin.nettest.nntool.android.app.util.info.Gatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;

import static at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware.NETWORK_WIFI;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalGatherer extends Gatherer<SignalGatherer.CurrentSignalStrength> {

    private final static String TAG = SignalGatherer.class.getSimpleName();

    private final NetworkStateBroadcastReceiver networkStateBroadcastReceiver = new NetworkStateBroadcastReceiver();

    private final TelephonyStateListener telephonyStateListener = new TelephonyStateListener();

    private final AtomicReference<SignalItem> lastSignalItem = new AtomicReference<>(null);

    public static class CurrentSignalStrength {
        final SignalItem.SignalType signalType;
        final int signal;
        final int lteRsrq;

        public CurrentSignalStrength(final SignalItem.SignalType signalType, final int signal) {
            this(signalType, signal, SignalItem.UNKNOWN);
        }

        public CurrentSignalStrength(final SignalItem.SignalType signalType, final int signal, final int lteRsrq) {
            this.signalType = signalType;
            this.signal = signal;
            this.lteRsrq = lteRsrq;
        }

        public SignalItem.SignalType getSignalType() {
            return signalType;
        }

        public int getSignal() {
            return signal;
        }

        public int getLteRsrq() {
            return lteRsrq;
        }

        @Override
        public String toString() {
            return "CurrentSignalStrength{" +
                    "signalType=" + signalType +
                    ", signal=" + signal +
                    ", lteRsrq=" + lteRsrq +
                    '}';
        }
    }

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

    public SignalItem getLastSignalItem() {
        return lastSignalItem.get();
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
                    setCurrentValue(new CurrentSignalStrength(SignalItem.SignalType.WIFI, rssi));
                }
            }
        }
    }

    private class TelephonyStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            if (signalStrength != null) {
                Log.d(TAG, signalStrength.toString());
            }

            final int network = getNetwork();
            int strength = SignalItem.UNKNOWN;
            int lteRsrp = SignalItem.UNKNOWN;
            int lteRsrq = SignalItem.UNKNOWN;
            int lteRsssnr = SignalItem.UNKNOWN;
            int lteCqi = SignalItem.UNKNOWN;
            int errorRate = SignalItem.UNKNOWN;


            // discard signal strength from GT-I9100G (Galaxy S II) - passes wrong info
            if (android.os.Build.MODEL != null)  {
                if (android.os.Build.MODEL.equals("GT-I9100G")
                        || android.os.Build.MODEL.equals("HUAWEI P2-6011")) {
                    return;
                }
            }

            if (network != NetworkTypeAware.NETWORK_ETHERNET
                    && network != NetworkTypeAware.NETWORK_WIFI
                    && network != NetworkTypeAware.NETWORK_BLUETOOTH) {

                if (signalStrength != null) {
                    if (network == TelephonyManager.NETWORK_TYPE_CDMA) {
                        strength = signalStrength.getCdmaDbm();
                    }
                    else if (network == TelephonyManager.NETWORK_TYPE_EVDO_0
                            || network == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                        strength = signalStrength.getEvdoDbm();
                    }
                    else if (network == 13) {
                        try {
                            lteRsrp = (Integer) SignalStrength.class.getMethod("getLteRsrp").invoke(signalStrength);
                            lteRsrq = (Integer) SignalStrength.class.getMethod("getLteRsrq").invoke(signalStrength);
                            lteRsssnr = (Integer) SignalStrength.class.getMethod("getLteRssnr").invoke(signalStrength);
                            lteCqi = (Integer) SignalStrength.class.getMethod("getLteCqi").invoke(signalStrength);

                            if (lteRsrp == Integer.MAX_VALUE) {
                                lteRsrp = SignalItem.UNKNOWN;
                            }
                            if (lteRsrq == Integer.MAX_VALUE) {
                                lteRsrq = SignalItem.UNKNOWN;
                            }
                            if (lteRsrq > 0) {
                                lteRsrq = -lteRsrq; // fix invalid rsrq values for some devices
                            }
                            if (lteRsssnr == Integer.MAX_VALUE) {
                                lteRsssnr = SignalItem.UNKNOWN;
                            }
                            if (lteCqi == Integer.MAX_VALUE) {
                                lteCqi = SignalItem.UNKNOWN;
                            }
                        }
                        catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    else if (signalStrength.isGsm()) {
                        try {
                            final Method getGsmDbm = SignalStrength.class.getMethod("getGsmDbm");
                            final Integer result = (Integer) getGsmDbm.invoke(signalStrength);
                            if (result != -1) {
                                strength = result;
                            }
                        }
                        catch (Throwable t) { }

                        if (strength == SignalItem.UNKNOWN) {   // fallback if not implemented
                            int dBm;
                            int gsmSignalStrength = signalStrength.getGsmSignalStrength();
                            int asu = (gsmSignalStrength == 99 ? -1 : gsmSignalStrength);

                            if (asu != -1) {
                                dBm = -113 + (2 * asu);
                            }
                            else {
                                dBm = SignalItem.UNKNOWN;
                            }

                            strength = dBm;
                        }
                        errorRate = signalStrength.getGsmBitErrorRate();
                    }

                    if (lteRsrp != SignalItem.UNKNOWN) {
                        setCurrentValue(new CurrentSignalStrength(SignalItem.SignalType.RSRP, lteRsrp, lteRsrq));
                    }
                    else {
                        setCurrentValue(new CurrentSignalStrength(SignalItem.SignalType.MOBILE, strength));
                    }
                }

                final SignalItem signalItem = SignalItem.getCellSignalItem(network, strength, errorRate, lteRsrp, lteRsrq, lteRsssnr, lteCqi);
                lastSignalItem.set(signalItem);
                //System.out.println(getTelephonyManager().getAllCellInfo().get(0));
            }
        }
    }
}
