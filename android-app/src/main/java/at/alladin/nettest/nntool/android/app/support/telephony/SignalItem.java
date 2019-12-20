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

package at.alladin.nettest.nntool.android.app.support.telephony;

import at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware;

/**
 * old signal model
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalItem {
    public final static int UNKNOWN = Integer.MIN_VALUE;

    public final Long timeStampMillis;
    public final Long timeStampNano;
    public final Integer networkId;
    public final Integer signalStrength;
    public final Integer gsmBitErrorRate;
    public final Integer wifiLinkSpeed;
    public final Integer wifiRssi;

    public final Integer lteRsrp;
    public final Integer lteRsrq;
    public final Integer lteRssnr;
    public final Integer lteCqi;

    public static SignalItem getWifiSignalItem(final int wifiLinkSpeed, final int wifiRssi) {
        return new SignalItem(NetworkTypeAware.NETWORK_WLAN, UNKNOWN, UNKNOWN, wifiLinkSpeed, wifiRssi, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
    }

    public static SignalItem getCellSignalItem(final int networkId, final int signalStrength, final int gsmBitErrorRate,
                                               final int lteRsrp, final int lteRsrq, final int lteRssnr, final int lteCqi) {
        return new SignalItem(networkId, signalStrength, gsmBitErrorRate, UNKNOWN, UNKNOWN, lteRsrp, lteRsrq, lteRssnr, lteCqi);
    }

    private SignalItem(final int networkId, final int signalStrength, final int gsmBitErrorRate,
                       final int wifiLinkSpeed, final int wifiRssi, final int lteRsrp,
                       final int lteRsrq, final int lteRssnr, final int lteCqi) {
        this.timeStampMillis = System.currentTimeMillis();
        this.timeStampNano = System.nanoTime();
        this.networkId = CellInfoWrapper.minAndMaxIntegerToNull(networkId);
        this.signalStrength = CellInfoWrapper.minAndMaxIntegerToNull(signalStrength);
        this.gsmBitErrorRate = CellInfoWrapper.minAndMaxIntegerToNull(gsmBitErrorRate);
        this.wifiLinkSpeed = CellInfoWrapper.minAndMaxIntegerToNull(wifiLinkSpeed);
        this.wifiRssi = CellInfoWrapper.minAndMaxIntegerToNull(wifiRssi);
        this.lteRsrp = CellInfoWrapper.minAndMaxIntegerToNull(lteRsrp);
        this.lteRsrq = CellInfoWrapper.minAndMaxIntegerToNull(lteRsrq);
        this.lteRssnr = CellInfoWrapper.minAndMaxIntegerToNull(lteRssnr);
        this.lteCqi = CellInfoWrapper.minAndMaxIntegerToNull(lteCqi);
    }

    @Override
    public String toString() {
        return "SignalItem{" +
                "timeStampMillis=" + timeStampMillis +
                ", timeStampNano=" + timeStampNano +
                ", networkId=" + networkId +
                ", signalStrength=" + signalStrength +
                ", gsmBitErrorRate=" + gsmBitErrorRate +
                ", wifiLinkSpeed=" + wifiLinkSpeed +
                ", wifiRssi=" + wifiRssi +
                ", lteRsrp=" + lteRsrp +
                ", lteRsrq=" + lteRsrq +
                ", lteRssnr=" + lteRssnr +
                ", lteCqi=" + lteCqi +
                '}';
    }
}