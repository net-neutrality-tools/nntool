package at.alladin.nettest.nntool.android.app.util.info.signal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import at.alladin.nettest.nntool.android.app.support.telephony.CellInfoWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.CellSignalStrengthWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.SignalItem;
import at.alladin.nettest.nntool.android.app.util.PermissionUtil;
import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware;

import static at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware.NETWORK_WLAN;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalGatherer extends ListenableGatherer<SignalStrengthChangeEvent, SignalStrengthChangeListener> {

    private final static String TAG = SignalGatherer.class.getSimpleName();

    private final NetworkStateBroadcastReceiver networkStateBroadcastReceiver = new NetworkStateBroadcastReceiver();

    private final TelephonyStateListener telephonyStateListener = new TelephonyStateListener();

    private final AtomicReference<CellSignalStrengthWrapper> lastSignalItem = new AtomicReference<>(null);

    @Override
    public void onStart() {
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        getInformationProvider().getContext().registerReceiver(networkStateBroadcastReceiver, intentFilter);

        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
        getTelephonyManager().listen(telephonyStateListener, events);

        try {
            CellLocation.requestLocationUpdate();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        getInformationProvider().getContext().unregisterReceiver(networkStateBroadcastReceiver);
        getTelephonyManager().listen(telephonyStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public void addListener(final SignalStrengthChangeListener listener) {
        super.addListener(listener);
        if (listener != null) {
            listener.onSignalStrengthChange(getCurrentValue());
        }
    }

    public CellSignalStrengthWrapper getLastSignalItem() {
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
            Log.d(TAG, "WLAN CHANGE: " + intent + ", EXTRAS: " + intent.getExtras());
            if (getNetwork() == NETWORK_WLAN) {
                final WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
                final int rssi = wifiInfo.getRssi();
                if (rssi != -1 && rssi >= ACCEPT_WIFI_RSSI_MIN)  {
                    final CellInfoWrapper cellInfoWrapper = CellInfoWrapper.fromWifiInfo(wifiInfo);
                    setCurrentValue(
                            new SignalStrengthChangeEvent(
                                    CurrentSignalStrength.fromCellInfoWrapper(cellInfoWrapper)));
                }
            }
        }
    }

    private class TelephonyStateListener extends PhoneStateListener {

        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfoList) {
            Log.d(TAG, "------- onCellInfoChanged ---------");

            if (cellInfoList == null) {
                return;
            }

            for (final CellInfo cellInfo : cellInfoList) {
                if (cellInfo.isRegistered()) {
                    setCurrentValue(
                            new SignalStrengthChangeEvent(
                                    CurrentSignalStrength.fromCellInfoWrapper(CellInfoWrapper.fromCellInfo(cellInfo))));

                    setLastSignalItem(CellSignalStrengthWrapper.fromCellInfo(cellInfo));
                    break;
                }
            }
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            if (signalStrength != null) {
                Log.d(TAG, "Got new signalStrength: " + signalStrength.toString());
            }

            final int network = getNetwork();
            int strength = SignalItem.UNKNOWN;
            int lteRsrp = SignalItem.UNKNOWN;
            int lteRsrq = SignalItem.UNKNOWN;
            int lteRsssnr = SignalItem.UNKNOWN;
            int lteCqi = SignalItem.UNKNOWN;
            int errorRate = SignalItem.UNKNOWN;

            // discard signalDbm strength from GT-I9100G (Galaxy S II) - passes wrong info
            if (android.os.Build.MODEL != null)  {
                if (android.os.Build.MODEL.equals("GT-I9100G")
                        || android.os.Build.MODEL.equals("HUAWEI P2-6011")) {
                    return;
                }
            }

            if (network != NetworkTypeAware.NETWORK_ETHERNET
                    && network != NetworkTypeAware.NETWORK_WLAN
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

                    final SignalItem signalItem = SignalItem.getCellSignalItem(network, strength, errorRate, lteRsrp, lteRsrq, lteRsssnr, lteCqi);
                    final CellInfoWrapper cellInfoWrapper = CellInfoWrapper.fromSignalItem(signalItem);

                    if (signalItem.lteRsrp != null) {
                        setCurrentValue(
                                new SignalStrengthChangeEvent(
                                        CurrentSignalStrength.fromCellInfoWrapper(cellInfoWrapper)));
                    }
                    else {
                        setCurrentValue(
                                new SignalStrengthChangeEvent(
                                        CurrentSignalStrength.fromCellInfoWrapper(cellInfoWrapper)));
                    }

                    setLastSignalItem(cellInfoWrapper.getCellSignalStrengthWrapper());
                }

                /*
                final SignalItem signalItem = SignalItem.getCellSignalItem(network, strength, errorRate, lteRsrp, lteRsrq, lteRsssnr, lteCqi);
                final CellSignalStrengthWrapper signalStrengthWrapper = CellSignalStrengthWrapper.fromSignalItem(signalItem);
                signalStrengthWrapper.setNetworkId(network);
                setLastSignalItem(signalStrengthWrapper);
                */

                if (PermissionUtil.isLocationPermissionGranted(getInformationProvider().getContext())) {
                    onCellInfoChanged(getTelephonyManager().getAllCellInfo());
                }
                else {
                    Log.w(TAG, "Location permission not granted. Cannot read cell location info.");
                }

                //System.out.println(getTelephonyManager().getAllCellInfo().get(0));
            }
        }
    }

    private void dispatchSignalStrengthChangedEvent(final SignalStrengthChangeEvent event) {
        if (getListenerList() != null) {
            for (final SignalStrengthChangeListener listener : getListenerList()) {
                listener.onSignalStrengthChange(event);
            }
        }
    }

    public void setLastSignalItem(final CellSignalStrengthWrapper signalItem) {
        lastSignalItem.set(signalItem);
        Log.d(TAG, "New signalDbm item: " + lastSignalItem.get());
    }

    @Override
    public void setCurrentValue(SignalStrengthChangeEvent currentValue) {
        super.setCurrentValue(currentValue);
        dispatchSignalStrengthChangedEvent(currentValue);
    }

    /*
    @Override
    public void onLocationChanged(GeoLocationChangeEvent geoLocationChangeEvent) {
        Log.d(TAG, "Location change event: " + geoLocationChangeEvent);
        if (geoLocationChangeEvent != null
                && GeoLocationChangeEvent.GeoLocationChangeEventType.ENABLED.equals(geoLocationChangeEvent.getEventType())) {
            final Intent intent = new Intent();
            intent.putExtra("GPS_ENABLED", true);
            networkStateBroadcastReceiver.onReceive(getInformationProvider().getContext(), intent);
        }
    }
    */
}