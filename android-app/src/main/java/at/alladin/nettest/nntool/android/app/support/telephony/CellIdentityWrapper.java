package at.alladin.nettest.nntool.android.app.support.telephony;

import android.net.wifi.WifiInfo;
import android.os.Build;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class CellIdentityWrapper {

	CellType cellInfoType;

	private Integer cellId;
	private Integer physicalCellId;
	private Integer areaCode;
	private Integer scramblingCode;
	private Integer mcc;
	private Integer mnc;
	private Integer frequency;
	private boolean isRegistered = false;

	private CellIdentityWrapper(final CellInfo cellInfo) {
		isRegistered = cellInfo.isRegistered();
	}

	private CellIdentityWrapper() {
		isRegistered = false;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfo cellInfo) {
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

	public static CellIdentityWrapper fromCellInfo(final CellInfoLte cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_LTE);

		final CellIdentityLte cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(maxIntegerToNull(cellIdentity.getTac()));
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getCi()));
		wrapper.setMcc(maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(maxIntegerToNull(cellIdentity.getMnc()));
		wrapper.setPhysicalCellId(maxIntegerToNull(cellIdentity.getPci()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			wrapper.setFrequency(cellIdentity.getEarfcn());
		}

		return wrapper;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfoGsm cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_GSM);

		final CellIdentityGsm cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(maxIntegerToNull(cellIdentity.getLac()));
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getCid()));
		wrapper.setMcc(maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(maxIntegerToNull(cellIdentity.getMnc()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			wrapper.setFrequency(cellIdentity.getArfcn());
		}

		return wrapper;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfoCdma cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_CDMA);

		final CellIdentityCdma cellIdentity = cellInfo.getCellIdentity();
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getBasestationId()));
		return wrapper;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfoWcdma cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_WCDMA);

		final CellIdentityWcdma cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(maxIntegerToNull(cellIdentity.getLac()));
		wrapper.setCellId(maxIntegerToNull(cellIdentity.getCid()));
		wrapper.setMcc(maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(maxIntegerToNull(cellIdentity.getMnc()));
		wrapper.setScramblingCode(maxIntegerToNull(cellIdentity.getPsc()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			wrapper.setFrequency(cellIdentity.getUarfcn());
		}

		return wrapper;
	}

	public static CellIdentityWrapper fromWifiInfo(final WifiInfo wifiInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper();
		wrapper.setRegistered(true);
		wrapper.setCellInfoType(CellType.WIFI);
		wrapper.setFrequency(maxIntegerToNull(wifiInfo.getFrequency()));
		return wrapper;
	}

	public static CellIdentityWrapper fromSignalItem(final SignalItem signalItem) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper();
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

	public void setCellInfoType(CellType cellInfoType) {
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

	public CellType getCellInfoType() {
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

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean registered) {
		isRegistered = registered;
	}
}
