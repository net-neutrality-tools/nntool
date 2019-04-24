package at.alladin.nettest.nntool.android.app.support.telephony;

import android.net.wifi.WifiInfo;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

public class CellInfoWrapper {

	public enum CellInfoType {
		MOBILE_CDMA, /* MOBILE 2G */
		MOBILE_GSM, /* MOBILE 2G */
		MOBILE_LTE, /* MOBILE 4G */
		MOBILE_WCDMA, /* MOBILE 3G */
		WIFI /* WIFI */
	}

	CellInfoType cellInfoType;

	private Integer cellId;

	private Integer physicalCellId;

	private Integer areaCode;

	private Integer scramblingCode;

	private Integer mcc;

	private Integer mnc;

	private Integer frequency;

	private CellInfoWrapper() {

	}

	public static CellInfoWrapper fromCellInfo(final CellInfo cellInfo) {
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

	public static CellInfoWrapper fromCellInfo(final CellInfoLte cellInfo) {
		final CellInfoWrapper wrapper = new CellInfoWrapper();
		wrapper.setCellInfoType(CellInfoType.MOBILE_LTE);

		final CellIdentityLte cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(maxIntegerToNull(cellIdentity.getTac()));
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getCi()));
		wrapper.setMcc(maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(maxIntegerToNull(cellIdentity.getMnc()));
		wrapper.setPhysicalCellId(maxIntegerToNull(cellIdentity.getPci()));

		return wrapper;
	}

	public static CellInfoWrapper fromCellInfo(final CellInfoGsm cellInfo) {
		final CellInfoWrapper wrapper = new CellInfoWrapper();
		wrapper.setCellInfoType(CellInfoType.MOBILE_GSM);

		final CellIdentityGsm cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(maxIntegerToNull(cellIdentity.getLac()));
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getCid()));
		wrapper.setMcc(maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(maxIntegerToNull(cellIdentity.getMnc()));

		return wrapper;
	}

	public static CellInfoWrapper fromCellInfo(final CellInfoCdma cellInfo) {
		final CellInfoWrapper wrapper = new CellInfoWrapper();
		wrapper.setCellInfoType(CellInfoType.MOBILE_CDMA);

		final CellIdentityCdma cellIdentity = cellInfo.getCellIdentity();
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getBasestationId()));
		return wrapper;
	}

	public static CellInfoWrapper fromCellInfo(final CellInfoWcdma cellInfo) {
		final CellInfoWrapper wrapper = new CellInfoWrapper();
		wrapper.setCellInfoType(CellInfoType.MOBILE_WCDMA);

		final CellIdentityWcdma cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(maxIntegerToNull(cellIdentity.getLac()));
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getCid()));
		wrapper.setMcc(maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(maxIntegerToNull(cellIdentity.getMnc()));
		wrapper.setScramblingCode(maxIntegerToNull(cellIdentity.getPsc()));

		return wrapper;
	}

	public static CellInfoWrapper fromWifiInfo(final WifiInfo wifiInfo) {
		final CellInfoWrapper wrapper = new CellInfoWrapper();
		wrapper.setCellInfoType(CellInfoType.WIFI);
		wrapper.setFrequency(maxIntegerToNull(wifiInfo.getFrequency()));
		return wrapper;
	}

	public static Integer maxIntegerToNull(final Integer i) {
		return i == null ? null : (i != Integer.MAX_VALUE) ? i : null;
	}


	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public void setMcc(Integer mcc) {
		this.mcc = mcc;
	}

	public void setMnc(Integer mnc) {
		this.mnc = mnc;
	}

	public void setCellInfoType(CellInfoType cellInfoType) {
		this.cellInfoType = cellInfoType;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	public void setAreaCode(Integer areaCode) {
		this.areaCode = areaCode;
	}

	public void setScramblingCode(Integer scramblingCode) {
		this.scramblingCode = scramblingCode;
	}

	public void setPhysicalCellId(Integer physicalCellId) {
		this.physicalCellId = physicalCellId;
	}

	public CellInfoType getCellInfoType() {
		return cellInfoType;
	}

	public Integer getCellId() {
		return cellId;
	}

	public Integer getPhysicalCellId() {
		return physicalCellId;
	}

	public Integer getAreaCode() {
		return areaCode;
	}

	public Integer getScramblingCode() {
		return scramblingCode;
	}

	public Integer getMcc() {
		return mcc;
	}

	public Integer getMnc() {
		return mnc;
	}

	public Integer getFrequency() {
		return frequency;
	}
}
