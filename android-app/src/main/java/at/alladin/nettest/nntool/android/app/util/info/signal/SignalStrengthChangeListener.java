package at.alladin.nettest.nntool.android.app.util.info.signal;

import at.alladin.nettest.nntool.android.app.util.info.InformationServiceListener;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface SignalStrengthChangeListener extends InformationServiceListener {

    void onSignalStrengthChange(final SignalStrengthChangeEvent event);
}
