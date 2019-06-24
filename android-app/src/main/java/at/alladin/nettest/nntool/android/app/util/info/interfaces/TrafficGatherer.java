package at.alladin.nettest.nntool.android.app.util.info.interfaces;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.nntool.android.app.util.info.RunnableGatherer;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class TrafficGatherer
        extends ListenableGatherer<CurrentInterfaceTraffic, InterfaceTrafficUpdateListener>
        implements RunnableGatherer {

    private final static String TAG = TrafficGatherer.class.getSimpleName();

    private final static Interval INTERVAL = new Interval(2, TimeUnit.SECONDS);

    private final TrafficServiceImpl trafficService = new TrafficServiceImpl();

    @Override
    public void onStart() {
        Log.d(TAG, "Start TrafficGatherer");
        if (trafficService != null) {
            trafficService.start();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Stop TrafficGatherer");
        if (trafficService != null) {
            trafficService.stop();
        }
    }

    @Override
    public Interval getInterval() {
        return INTERVAL;
    }

    @Override
    public void run() {
        if (trafficService != null) {
            trafficService.stop();
            final CurrentInterfaceTraffic currentInterfaceTraffic =
                    new CurrentInterfaceTraffic(trafficService.getRxBytes(),
                            trafficService.getTxBytes(), trafficService.getDurationNs());
            setCurrentValue(currentInterfaceTraffic);
            emitUpdateEvent(currentInterfaceTraffic);
            trafficService.start();
        }
    }

    protected void emitUpdateEvent(final CurrentInterfaceTraffic currentInterfaceTraffic) {
        Log.d(TAG, currentInterfaceTraffic.toString());

        for (final InterfaceTrafficUpdateListener listener : getListenerList()) {
            listener.onTrafficUpdate(currentInterfaceTraffic);
        }
    }
}
