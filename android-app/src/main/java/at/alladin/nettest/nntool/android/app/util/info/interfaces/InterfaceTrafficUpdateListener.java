package at.alladin.nettest.nntool.android.app.util.info.interfaces;

import at.alladin.nettest.nntool.android.app.util.info.InformationServiceListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface InterfaceTrafficUpdateListener extends InformationServiceListener {

    void onTrafficUpdate(final CurrentInterfaceTraffic currentInterfaceTraffic);
}
