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
import at.alladin.nettest.nntool.android.app.support.telephony.CellType;
import at.alladin.nettest.nntool.android.app.support.telephony.SignalItem;
import at.alladin.nettest.nntool.android.app.util.PermissionUtil;
import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeEvent;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkChangeListener;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkGatherer;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware;

import static at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware.NETWORK_WLAN;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalGatherer extends ListenableGatherer<SignalStrengthChangeEvent, SignalStrengthChangeListener> implements NetworkChangeListener {

    private final static String TAG = SignalGatherer.class.getSimpleName();

    private final NetworkStateBroadcastReceiver networkStateBroadcastReceiver = new NetworkStateBroadcastReceiver();

    private final TelephonyStateListener telephonyStateListener = new TelephonyStateListener();

    private final AtomicReference<CellSignalStrengthWrapper> lastSignalItem = new AtomicReference<>(null);

    @Override
    public void onStart() {
        IntentFilter intentFilter;
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        //intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        getInformationProvider().getContext().registerReceiver(networkStateBroadcastReceiver, intentFilter);

        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;
        getTelephonyManager().listen(telephonyStateListener, events);

        try {
            NetworkGatherer ng = getInformationProvider().getGatherer(NetworkGatherer.class);
            if (ng == null) {
                Log.d(TAG, "NetworkGatherer not found. Registering...");
                ng = getInformationProvider().registerGatherer(NetworkGatherer.class);
            }
            ng.addListener(this);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }


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

        final NetworkGatherer ng = getInformationProvider().getGatherer(NetworkGatherer.class);
        if (ng != null) {
            ng.removeListener(this);
        }
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

    @Override
    public void onNetworkChange(NetworkChangeEvent event) {
        Log.d(TAG, "Network changed: " + event);
        if (event != null) {
            boolean resetSignal = false;
            final CellSignalStrengthWrapper lastSignal = lastSignalItem.get();

            if (NetworkChangeEvent.NetworkChangeEventType.NO_CONNECTION.equals(event.getEventType())) {
                resetSignal = true;
            }
            else if (lastSignal != null
                    && NetworkChangeEvent.NetworkChangeEventType.SIGNAL_UPDATE.equals(event.getEventType())
                    && event.getNetworkType() != null) {
                final CellType newCellType = CellType.fromTelephonyNetworkTypeId(event.getNetworkType());
                final CellType oldCellType = lastSignal != null ? CellType.fromTelephonyNetworkTypeId(lastSignal.getNetworkId()) : CellType.UNKNOWN;
                Log.d(TAG, "comparing: (new) " + newCellType + " [network: " + event.getNetworkType() + "] <-> (old) " + oldCellType);
                resetSignal = !newCellType.getTechnologyType().equals(oldCellType.getTechnologyType());
            }
            else if (event.getNetworkType() == null) {
                resetSignal = true;
            }

            if (resetSignal) {
                Log.d(TAG, "RESET SIGNAL = true");
                setLastSignalItem(null);
                final SignalStrengthChangeEvent signalEvent = new SignalStrengthChangeEvent(null);
                setCurrentValue(signalEvent);
            }
        }
    }

    private class NetworkStateBroadcastReceiver extends BroadcastReceiver {
        private final Integer ACCEPT_WIFI_RSSI_MIN = -113;

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int network = getNetwork();
            Log.d(TAG, "WLAN CHANGE [network: " + network + "] -> " + intent + ", EXTRAS: " + intent.getExtras());
            if (network == NetworkTypeAware.NETWORK_WLAN) {
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

            final int network = getNetwork();
            if (network == NetworkTypeAware.NETWORK_ETHERNET
                    || network == NetworkTypeAware.NETWORK_WLAN
                    || network == NetworkTypeAware.NETWORK_BLUETOOTH) {
                return;
            }

            final CellType currentCellType = CellType.fromTelephonyNetworkTypeId(network);

            for (final CellInfo cellInfo : cellInfoList) {
                Log.d(TAG, "CellInfo: " + cellInfo);
                if (cellInfo.isRegistered()) {
                    final CellInfoWrapper cellInfoWrapper = CellInfoWrapper.fromCellInfo(cellInfo);
                    cellInfoWrapper.getCellSignalStrengthWrapper().setNetworkId(network);

                    //check if active cell type is the same as from the registered cell
                    if (!cellInfoWrapper.getCellIdentityWrapper().getCellInfoType().equals(currentCellType)) {
                        Log.d(TAG, "Active cell type not matching registered cell type: "
                                + currentCellType + " <-> " + cellInfoWrapper.getCellIdentityWrapper().getCellInfoType());
                        continue;
                    }

                    setCurrentValue(
                            new SignalStrengthChangeEvent(
                                    CurrentSignalStrength.fromCellInfoWrapper(cellInfoWrapper)));
                    setLastSignalItem(cellInfoWrapper.getCellSignalStrengthWrapper());
                    break;
                }
            }
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {

            final int network = getNetwork();

            if (signalStrength != null) {
                Log.d(TAG, "Got new signalStrength [network: " + network + "] -> " + signalStrength.toString());
            }

            if (network == NetworkTypeAware.NETWORK_ETHERNET
                    || network == NetworkTypeAware.NETWORK_WLAN
                    || network == NetworkTypeAware.NETWORK_BLUETOOTH) {
                Log.d(TAG, "Ignoring SignalStrengthChanged because network type is not cellular.");
                return;
            }

            int strength = SignalItem.UNKNOWN;
            int lteRsrp = SignalItem.UNKNOWN;
            int lteRsrq = SignalItem.UNKNOWN;
            int lteRsssnr = SignalItem.UNKNOWN;
            int lteCqi = SignalItem.UNKNOWN;
            int errorRate = SignalItem.UNKNOWN;

            // discard signalDbm strength from GT-I9100G (Galaxy S II) - passes wrong info
            if (android.os.Build.MODEL != null) {
                if (android.os.Build.MODEL.equals("GT-I9100G")
                        || android.os.Build.MODEL.equals("HUAWEI P2-6011")) {
                    return;
                }
            }

            if (network != NetworkTypeAware.NETWORK_ETHERNET
                    && network != NetworkTypeAware.NETWORK_WLAN
                    && network != NetworkTypeAware.NETWORK_BLUETOOTH) {

                CellInfoWrapper fallbackCellInfoWrapper = null;

                if (signalStrength != null) {
                    if (network == TelephonyManager.NETWORK_TYPE_CDMA) {
                        strength = signalStrength.getCdmaDbm();
                    } else if (network == TelephonyManager.NETWORK_TYPE_EVDO_0
                            || network == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                        strength = signalStrength.getEvdoDbm();
                    } else if (network == 13) {
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
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else if (signalStrength.isGsm()) {
                        try {
                            final Method getGsmDbm = SignalStrength.class.getMethod("getGsmDbm");
                            final Integer result = (Integer) getGsmDbm.invoke(signalStrength);
                            if (result != -1) {
                                strength = result;
                            }
                        } catch (Throwable t) {
                        }

                        if (strength == SignalItem.UNKNOWN) {   // fallback if not implemented
                            int dBm;
                            int gsmSignalStrength = signalStrength.getGsmSignalStrength();
                            int asu = (gsmSignalStrength == 99 ? -1 : gsmSignalStrength);

                            if (asu != -1) {
                                dBm = -113 + (2 * asu);
                            } else {
                                dBm = SignalItem.UNKNOWN;
                            }

                            strength = dBm;
                        }
                        errorRate = signalStrength.getGsmBitErrorRate();
                    }

                    final SignalItem signalItem = SignalItem.getCellSignalItem(network, strength, errorRate, lteRsrp, lteRsrq, lteRsssnr, lteCqi);
                    fallbackCellInfoWrapper = CellInfoWrapper.fromSignalItem(signalItem);
                }

                final CellSignalStrengthWrapper previousSignalItem = getLastSignalItem();

                if (PermissionUtil.isLocationPermissionGranted(getInformationProvider().getContext())) {
                    onCellInfoChanged(getTelephonyManager().getAllCellInfo());
                }
                else {
                    Log.w(TAG, "Location permission not granted. Cannot read cell location info.");
                }

                if (fallbackCellInfoWrapper != null
                        && (getLastSignalItem() == null || (getLastSignalItem().equals(previousSignalItem)))) {
                    Log.d(TAG, "Signal Strength could not be updated. Fallback to old signal strength API.");
                    setCurrentValue(
                            new SignalStrengthChangeEvent(
                                    CurrentSignalStrength.fromCellInfoWrapper(fallbackCellInfoWrapper)));

                    setLastSignalItem(fallbackCellInfoWrapper.getCellSignalStrengthWrapper());
                }

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