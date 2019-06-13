package at.alladin.nettest.nntool.android.app.util.info.interfaces;

import android.net.TrafficStats;

import at.alladin.nntool.client.v2.task.service.TrafficService;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class TrafficServiceImpl implements TrafficService {

    private long trafficRxStart = -1;

    private long trafficTxStart = -1;

    private long trafficRxEnd = -1;

    private long trafficTxEnd = -1;

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.service.TrafficService#start()
     */
    @Override
    public int start() {
        if ((trafficRxStart = TrafficStats.getTotalRxBytes()) == TrafficStats.UNSUPPORTED) {
            return SERVICE_NOT_SUPPORTED;
        }
        trafficTxStart = TrafficStats.getTotalTxBytes();
        return SERVICE_START_OK;
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.service.TrafficService#getTxBytes()
     */
    @Override
    public long getTxBytes() {
        return (trafficTxStart != -1 ? trafficTxEnd - trafficTxStart : -1);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.service.TrafficService#getRxBytes()
     */
    @Override
    public long getRxBytes() {
        return (trafficRxStart != -1 ? trafficRxEnd - trafficRxStart : -1);
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.service.TrafficService#stop()
     */
    @Override
    public void stop() {
        trafficTxEnd = TrafficStats.getTotalTxBytes();
        trafficRxEnd = TrafficStats.getTotalRxBytes();
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.service.TrafficService#getTotalTxBytes()
     */
    @Override
    public long getTotalTxBytes() {
        return TrafficStats.getTotalTxBytes();
    }

    /*
     * (non-Javadoc)
     * @see at.alladin.rmbt.client.v2.task.service.TrafficService#getTotalRxBytes()
     */
    @Override
    public long getTotalRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }
}
