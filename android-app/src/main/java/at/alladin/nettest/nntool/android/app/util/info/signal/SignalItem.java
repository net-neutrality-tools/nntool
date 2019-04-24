package at.alladin.nettest.nntool.android.app.util.info.signal;

import at.alladin.nettest.nntool.android.app.support.telephony.CellInfoWrapper;
import at.alladin.nettest.nntool.android.app.util.info.network.NetworkTypeAware;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SignalItem {
    public final static int UNKNOWN = Integer.MIN_VALUE;

    public enum SignalType {
        MOBILE,
        WIFI,
        RSRP
    }

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
        return new SignalItem(NetworkTypeAware.NETWORK_WIFI, UNKNOWN, UNKNOWN, wifiLinkSpeed, wifiRssi, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
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
        this.networkId = CellInfoWrapper.maxIntegerToNull(networkId);
        this.signalStrength = CellInfoWrapper.maxIntegerToNull(signalStrength);
        this.gsmBitErrorRate = CellInfoWrapper.maxIntegerToNull(gsmBitErrorRate);
        this.wifiLinkSpeed = CellInfoWrapper.maxIntegerToNull(wifiLinkSpeed);
        this.wifiRssi = CellInfoWrapper.maxIntegerToNull(wifiRssi);
        this.lteRsrp = CellInfoWrapper.maxIntegerToNull(lteRsrp);
        this.lteRsrq = CellInfoWrapper.maxIntegerToNull(lteRsrq);
        this.lteRssnr = CellInfoWrapper.maxIntegerToNull(lteRssnr);
        this.lteCqi = CellInfoWrapper.maxIntegerToNull(lteCqi);
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