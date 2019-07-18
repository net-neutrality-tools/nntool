package at.alladin.nettest.nntool.android.app.util.info.network;

import org.joda.time.LocalDateTime;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public final class NetworkChangeEvent implements OperatorInfo {

    public static enum NetworkChangeEventType {
        NO_CONNECTION,
        SIGNAL_UPDATE
    }

    /**
     * System.nanoTime()
     */
    private WifiOperator wifiOperator;

    private MobileOperator mobileOperator;

    private final long timestampNs;

    private final LocalDateTime time;

    private final Integer networkType;

    private NetworkChangeEventType eventType;

    public NetworkChangeEvent(final Integer networkType) {
        this(networkType, NetworkChangeEventType.SIGNAL_UPDATE);
    }

    public NetworkChangeEvent(final Integer networkType, NetworkChangeEventType type) {
        this.networkType = networkType;
        this.timestampNs = System.nanoTime();
        this.time = LocalDateTime.now();
        this.eventType = type;
    }

    public LocalDateTime getTime() {
        return time;
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

    public void setEventType(NetworkChangeEventType eventType) {
        this.eventType = eventType;
    }

    public NetworkChangeEventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "NetworkChangeEvent{" +
                "wifiOperator=" + wifiOperator +
                ", mobileOperator=" + mobileOperator +
                ", timestampNs=" + timestampNs +
                ", networkType=" + networkType +
                ", eventType=" + eventType +
                '}';
    }

    @Override
    public String getOperatorName() {
        if (getNetworkType() == NetworkTypeAware.NETWORK_WLAN && getWifiOperator() != null) {
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
