package at.alladin.nettest.nntool.android.app.util.info;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface NetworkTypeAware {

    /** Returned by getNetwork() if Wifi */
    public static final int NETWORK_WIFI = 99;

    /** Returned by getNetwork() if Ethernet */
    public static final int NETWORK_ETHERNET = 106;

    /** Returned by getNetwork() if Bluetooth */
    public static final int NETWORK_BLUETOOTH = 107;

    int getNetwork();
}
