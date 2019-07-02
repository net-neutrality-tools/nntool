package at.alladin.nettest.nntool.android.app.util.info.system;

import at.alladin.nettest.nntool.android.app.util.info.InformationServiceListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface SystemInfoListener extends InformationServiceListener {

    void onSystemInfoUpdate(final SystemInfoEvent systemInfoEvent);
}
