package at.alladin.nettest.nntool.android.app.util.info.interfaces;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CurrentInterfaceTraffic {

    private Long rxBytes;

    private Long txBytes;

    private long durationNs;

    private Long rxBps;

    private Long txBps;

    public CurrentInterfaceTraffic() { }

    public CurrentInterfaceTraffic(final long rxBytes, final long txBytes, final long durationNs) {
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.durationNs = durationNs;
        this.rxBps = (long) (durationNs > 0 ? ((double)rxBytes * 8D) / ((double)durationNs / 1e9D) : 0);
        this.txBps = (long) (durationNs > 0 ? ((double)txBytes * 8D) / ((double)durationNs / 1e9D) : 0);
    }

    public long getDurationNs() {
        return durationNs;
    }

    public void setDurationNs(long durationNs) {
        this.durationNs = durationNs;
    }

    public Long getRxBytes() {
        return rxBytes;
    }

    public void setRxBytes(Long rxBytes) {
        this.rxBytes = rxBytes;
    }

    public Long getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(Long txBytes) {
        this.txBytes = txBytes;
    }

    /**
     * RX bit per sec
     * @return
     */
    public Long getRxBps() {
        return rxBps;
    }

    /**
     * TX bit per sec
     * @return
     */
    public Long getTxBps() {
        return txBps;
    }

    @Override
    public String toString() {
        return "CurrentInterfaceTraffic{" +
                "rxBytes=" + rxBytes +
                ", txBytes=" + txBytes +
                ", durationNs=" + durationNs +
                ", rxBps=" + rxBps +
                ", txBps=" + txBps +
                '}';
    }
}
