package at.alladin.nettest.nntool.android.app.util.info.network;

import at.alladin.nettest.nntool.android.app.util.info.InformationServiceListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface NetworkChangeListener extends InformationServiceListener {
    void onNetworkChange(NetworkChangeEvent event);
}
