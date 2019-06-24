package at.alladin.nettest.nntool.android.app.util.info.network;

import android.net.wifi.WifiInfo;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public final class WifiOperator implements OperatorInfo {
    final String ssid;

    final String bssid;

    final int networkId;

    public WifiOperator(final WifiInfo wifiInfo) {
        this.ssid = NetworkGatherer.removeQuotationsInCurrentSSIDForJellyBean(wifiInfo.getSSID());
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
