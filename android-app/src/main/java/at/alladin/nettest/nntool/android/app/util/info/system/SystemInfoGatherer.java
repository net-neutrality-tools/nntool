package at.alladin.nettest.nntool.android.app.util.info.system;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import at.alladin.nettest.nntool.android.app.util.info.ListenableGatherer;
import at.alladin.nettest.nntool.android.app.util.info.RunnableGatherer;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SystemInfoGatherer
        extends ListenableGatherer<CurrentSystemInfo, SystemInfoListener>
        implements RunnableGatherer {

    private final static String TAG = SystemInfoGatherer.class.getSimpleName();
    private final static Interval INTERVAL = new Interval(2, TimeUnit.SECONDS);

    private CpuInfoImpl cpuInfo;
    private MemInfoImpl memInfo;

    @Override
    public void onStart() {
        Log.d(TAG, "Start SystemInfoGatherer");
        cpuInfo = new CpuInfoImpl();
        memInfo = new MemInfoImpl();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Stop SystemInfoGatherer");
    }

    @Override
    public Interval getInterval() {
        return INTERVAL;
    }

    @Override
    public void run() {
        Float cpuUsage = null;
        if (cpuInfo != null) {
            final float[] cpus = cpuInfo.update(false);
            if (cpus != null && cpus.length > 0) {
                float total = 0f;
                for (float cpu : cpus) {
                    total += cpu;
                }

                cpuUsage = (total / (float)cpus.length);
            }

        }

        CurrentSystemInfo.MemUsage memUsage = null;
        if (memInfo != null) {
            memInfo.update();
            memUsage = new CurrentSystemInfo.MemUsage(memInfo.getTotalMem(), memInfo.getFreeMem());

        }

        final CurrentSystemInfo currentSystemInfo = new CurrentSystemInfo(cpuUsage, memUsage);
        emitUpdateEvent(currentSystemInfo);
    }

    protected void emitUpdateEvent(final CurrentSystemInfo currentSystemInfo) {
        Log.d(TAG, currentSystemInfo.toString());

        for (final SystemInfoListener listener : getListenerList()) {
            listener.onSystemInfoUpdate(new SystemInfoEvent(currentSystemInfo));
        }
    }
}
