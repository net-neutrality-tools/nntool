package at.alladin.nettest.nntool.android.app.support.telephony;

import android.net.wifi.WifiInfo;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;

/**
 * <p>
 *     Thanks to RTR and their open-rmbt source code to point out the "read signal strength from toString()" - solution.
 * 	(see:
 * 		<a href="https://github.com/rtr-nettest/open-rmbt/blob/master/RMBTAndroid/src/at/rtr/rmbt/android/util/CellInformationWrapper.java">
 * 	    	https://github.com/rtr-nettest/open-rmbt/blob/master/RMBTAndroid/src/at/rtr/rmbt/android/util/CellInformationWrapper.java
 * 	    </a>)
 * </p>
 *
 * <p>
 *     It seems like all signal strength KPIs are provided by the Android API by calling the respective {@link CellSignalStrength#toString()} method.
 *     The most reliable way is to parse the string returned by the toString() method,
 *     as java reflections may be slower and can fail due to missing getters and setters (though one can access fields)
 * </p>
 *
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class CellSignalStrengthWrapper {

	private Long timeStampMillis;
	private Long timeStampNano;

	private Integer networkId;
	private Integer signalStrength;
	private Integer gsmBitErrorRate;
	private Integer wifiLinkSpeed;
	private Integer wifiRssi;

	private Integer lteRsrp;
	private Integer lteRsrq;
	private Integer lteRssnr;
	private Integer lteCqi;

	private Integer timingAdvance;

	private CellSignalStrengthWrapper() {
		this.timeStampMillis = System.currentTimeMillis();
		this.timeStampNano = System.nanoTime();
	}

	public static CellSignalStrengthWrapper fromCellInfo(final CellInfo cellInfo) {
		if (cellInfo instanceof CellInfoLte) {
			return fromCellInfo((CellInfoLte) cellInfo);
		}
		else if (cellInfo instanceof CellInfoWcdma) {
			return fromCellInfo((CellInfoWcdma) cellInfo);
		}
		else if (cellInfo instanceof CellInfoGsm) {
			return fromCellInfo((CellInfoGsm) cellInfo);
		}
		else if (cellInfo instanceof CellInfoCdma) {
			return fromCellInfo((CellInfoCdma) cellInfo);
		}
		return null;
	}

	public static CellSignalStrengthWrapper fromCellInfo(final CellInfoLte cellInfo) {
		final CellSignalStrengthLte ss = cellInfo.getCellSignalStrength();
		final String desc = ss.toString();

		final CellSignalStrengthWrapper wrapper = new CellSignalStrengthWrapper();
		wrapper.setLteRsrp(getSignalStrengthValueFromDescriptionString(desc,"rsrp"));
		wrapper.setLteRsrq(getSignalStrengthValueFromDescriptionString(desc,"rsrq"));
		wrapper.setLteRssnr(getSignalStrengthValueFromDescriptionString(desc,"rssnr"));
		wrapper.setLteCqi(getSignalStrengthValueFromDescriptionString(desc,"cqi"));
		wrapper.setTimingAdvance(ss.getTimingAdvance());

		return wrapper;
	}

	public static CellSignalStrengthWrapper fromCellInfo(final CellInfoGsm cellInfo) {
		final CellSignalStrengthGsm ss = cellInfo.getCellSignalStrength();
		final String desc = ss.toString();

		final CellSignalStrengthWrapper wrapper = new CellSignalStrengthWrapper();
		wrapper.setSignalStrength(ss.getDbm());
		wrapper.setGsmBitErrorRate(getSignalStrengthValueFromDescriptionString(desc,"ber"));
		wrapper.setTimingAdvance(getSignalStrengthValueFromDescriptionString(desc,"mTa"));

		return wrapper;
	}

	public static CellSignalStrengthWrapper fromCellInfo(final CellInfoCdma cellInfo) {
		CellSignalStrengthCdma ss = cellInfo.getCellSignalStrength();

		final CellSignalStrengthWrapper wrapper = new CellSignalStrengthWrapper();
		wrapper.setSignalStrength(ss.getDbm());

		return wrapper;
	}

	public static CellSignalStrengthWrapper fromCellInfo(final CellInfoWcdma cellInfo) {
		final CellSignalStrengthWcdma ss = cellInfo.getCellSignalStrength();
		final String desc = ss.toString();

		final CellSignalStrengthWrapper wrapper = new CellSignalStrengthWrapper();
		wrapper.setSignalStrength(ss.getDbm());
		wrapper.setGsmBitErrorRate(getSignalStrengthValueFromDescriptionString(desc,"ber"));

		return wrapper;
	}

	public static CellSignalStrengthWrapper fromWifiInfo(final WifiInfo wifiInfo) {
		final CellSignalStrengthWrapper wrapper = new CellSignalStrengthWrapper();
		wrapper.setWifiRssi(wifiInfo.getRssi());
		wrapper.setWifiLinkSpeed(wifiInfo.getLinkSpeed());
		return wrapper;
	}

	public static CellSignalStrengthWrapper fromSignalItem(final SignalItem signalItem) {
		final CellSignalStrengthWrapper wrapper = new CellSignalStrengthWrapper();
		wrapper.setWifiLinkSpeed(signalItem.wifiLinkSpeed);
		wrapper.setWifiRssi(signalItem.wifiRssi);

		wrapper.setLteRsrp(signalItem.lteRsrp);
		wrapper.setLteCqi(signalItem.lteCqi);
		wrapper.setLteRsrq(signalItem.lteRsrq);
		wrapper.setLteRssnr(signalItem.lteRssnr);

		wrapper.setSignalStrength(signalItem.signalStrength);

		wrapper.setGsmBitErrorRate(signalItem.gsmBitErrorRate);
		wrapper.setNetworkId(signalItem.networkId);

		wrapper.setTimeStampMillis(wrapper.timeStampMillis);
		wrapper.setTimeStampNano(wrapper.timeStampNano);

		return wrapper;
	}

	/**
	 * @param description
	 * @param field
	 * @return
	 */
	private static Integer getSignalStrengthValueFromDescriptionString(String description, String field) {
		int index = description.indexOf(field + "=");
		if (index >= 0) {
			description = description.substring(index + field.length() + 1);
			int ret = Integer.parseInt(description.split(" ")[0]);
			return CellInfoWrapper.maxIntegerToNull(ret);
		}

		return null;
	}

	public Long getTimeStampMillis() {
		return timeStampMillis;
	}

	public void setTimeStampMillis(Long timeStampMillis) {
		this.timeStampMillis = timeStampMillis;
	}

	public Long getTimeStampNano() {
		return timeStampNano;
	}

	public void setTimeStampNano(Long timeStampNano) {
		this.timeStampNano = timeStampNano;
	}

	public Integer getNetworkId() {
		return networkId;
	}

	public void setNetworkId(Integer networkId) {
		this.networkId = networkId;
	}

	public Integer getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
	}

	public Integer getGsmBitErrorRate() {
		return gsmBitErrorRate;
	}

	public void setGsmBitErrorRate(Integer gsmBitErrorRate) {
		this.gsmBitErrorRate = gsmBitErrorRate;
	}

	public Integer getWifiLinkSpeed() {
		return wifiLinkSpeed;
	}

	public void setWifiLinkSpeed(Integer wifiLinkSpeed) {
		this.wifiLinkSpeed = wifiLinkSpeed;
	}

	public Integer getWifiRssi() {
		return wifiRssi;
	}

	public void setWifiRssi(Integer wifiRssi) {
		this.wifiRssi = wifiRssi;
	}

	public Integer getLteRsrp() {
		return lteRsrp;
	}

	public void setLteRsrp(Integer lteRsrp) {
		this.lteRsrp = lteRsrp;
	}

	public Integer getLteRsrq() {
		return lteRsrq;
	}

	public void setLteRsrq(Integer lteRsrq) {
		this.lteRsrq = lteRsrq;
	}

	public Integer getLteRssnr() {
		return lteRssnr;
	}

	public void setLteRssnr(Integer lteRssnr) {
		this.lteRssnr = lteRssnr;
	}

	public Integer getLteCqi() {
		return lteCqi;
	}

	public void setLteCqi(Integer lteCqi) {
		this.lteCqi = lteCqi;
	}

	public Integer getTimingAdvance() {
		return timingAdvance;
	}

	public void setTimingAdvance(Integer timingAdvance) {
		this.timingAdvance = timingAdvance;
	}

	@Override
	public String toString() {
		return "CellSignalStrengthWrapper{" +
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
				", timingAdvance=" + timingAdvance +
				'}';
	}
}
