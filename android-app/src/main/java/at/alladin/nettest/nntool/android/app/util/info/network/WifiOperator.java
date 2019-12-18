package at.alladin.nettest.nntool.android.app.util.info.network;

import android.net.wifi.WifiInfo;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public final class WifiOperator implements OperatorInfo {
    final String ssid;

    final String bssid;

    final int networkId;

    final int frequency;

    public WifiOperator(final WifiInfo wifiInfo) {
        this.ssid = NetworkGatherer.removeQuotationsInCurrentSSIDForJellyBean(wifiInfo.getSSID());
        this.bssid = wifiInfo.getBSSID();
        this.networkId = wifiInfo.getNetworkId();
        this.frequency = wifiInfo.getFrequency();
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

    public int getFrequency() {
        return frequency;
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
                ", networkId=" + networkId +
                ", frequency=" + frequency +
                '}';
    }
}
