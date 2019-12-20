/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
