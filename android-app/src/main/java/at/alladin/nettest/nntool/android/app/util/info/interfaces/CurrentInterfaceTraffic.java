package at.alladin.nettest.nntool.android.app.util.info.interfaces;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CurrentInterfaceTraffic {

    private Long rxBytes;

    private Long txBytes;

    public CurrentInterfaceTraffic() { }

    public CurrentInterfaceTraffic(final long rxBytes, final long txBytes) {
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
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

    @Override
    public String toString() {
        return "CurrentInterfaceTraffic{" +
                "rxBytes=" + rxBytes +
                ", txBytes=" + txBytes +
                '}';
    }
}
