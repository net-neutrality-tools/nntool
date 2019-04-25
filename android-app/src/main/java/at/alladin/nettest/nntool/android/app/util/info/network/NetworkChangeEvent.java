package at.alladin.nettest.nntool.android.app.util.info.network;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public final class NetworkChangeEvent implements OperatorInfo {

    /**
     * System.nanoTime()
     */
    private WifiOperator wifiOperator;

    private MobileOperator mobileOperator;

    private final boolean isConnected;

    private final long timestampNs;

    private final Integer networkType;

    public NetworkChangeEvent(final Integer networkType, final boolean isConnected) {
        this.networkType = networkType;
        this.timestampNs = System.nanoTime();
        this.isConnected = isConnected;
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

    public Integer getNetworkType() {
        return networkType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public String toString() {
        return "NetworkChangeEvent{" +
                "wifiOperator=" + wifiOperator +
                ", mobileOperator=" + mobileOperator +
                ", isConnected=" + isConnected +
                ", timestampNs=" + timestampNs +
                ", networkType=" + networkType +
                '}';
    }

    @Override
    public String getOperatorName() {
        if (getNetworkType() == NetworkTypeAware.NETWORK_WIFI && getWifiOperator() != null) {
            return getWifiOperator().getOperatorName();
        } else if (getNetworkType() == NetworkTypeAware.NETWORK_ETHERNET) {
            return "Ethernet";
        } else if (getNetworkType() == NetworkTypeAware.NETWORK_BLUETOOTH) {
            return "Bluetooth";
        } else if (getMobileOperator() != null) {
            return getMobileOperator().getOperatorName();
        }

        return "-";
    }
}
