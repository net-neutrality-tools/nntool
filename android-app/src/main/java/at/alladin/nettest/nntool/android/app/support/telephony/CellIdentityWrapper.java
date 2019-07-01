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
		wrapper.setAreaCode(CellInfoWrapper.maxIntegerToNull(cellIdentity.getTac()));
		wrapper.setCellId(CellInfoWrapper.maxIntegerToNull(cellIdentity.getCi()));
		wrapper.setMcc(CellInfoWrapper.maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(CellInfoWrapper.maxIntegerToNull(cellIdentity.getMnc()));
		wrapper.setPhysicalCellId(CellInfoWrapper.maxIntegerToNull(cellIdentity.getPci()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			wrapper.setFrequency(cellIdentity.getEarfcn());
		}

		return wrapper;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfoGsm cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_GSM);

		final CellIdentityGsm cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(CellInfoWrapper.maxIntegerToNull(cellIdentity.getLac()));
		wrapper.setCellId(CellInfoWrapper.maxIntegerToNull(cellIdentity.getCid()));
		wrapper.setMcc(CellInfoWrapper.maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(CellInfoWrapper.maxIntegerToNull(cellIdentity.getMnc()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			wrapper.setFrequency(cellIdentity.getArfcn());
		}

		return wrapper;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfoCdma cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_CDMA);

		final CellIdentityCdma cellIdentity = cellInfo.getCellIdentity();
		wrapper.setCellId(CellInfoWrapper.maxIntegerToNull(cellIdentity.getBasestationId()));
		return wrapper;
	}

	public static CellIdentityWrapper fromCellInfo(final CellInfoWcdma cellInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper(cellInfo);
		wrapper.setCellInfoType(CellType.MOBILE_WCDMA);

		final CellIdentityWcdma cellIdentity = cellInfo.getCellIdentity();
		wrapper.setAreaCode(CellInfoWrapper.maxIntegerToNull(cellIdentity.getLac()));
		wrapper.setCellId(CellInfoWrapper.maxIntegerToNull(cellIdentity.getCid()));
		wrapper.setMcc(CellInfoWrapper.maxIntegerToNull(cellIdentity.getMcc()));
		wrapper.setMnc(CellInfoWrapper.maxIntegerToNull(cellIdentity.getMnc()));
		wrapper.setScramblingCode(CellInfoWrapper.maxIntegerToNull(cellIdentity.getPsc()));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			wrapper.setFrequency(cellIdentity.getUarfcn());
		}

		return wrapper;
	}

	public static CellIdentityWrapper fromWifiInfo(final WifiInfo wifiInfo) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper();
		wrapper.setRegistered(true);
		wrapper.setCellInfoType(CellType.WLAN);
		wrapper.setFrequency(CellInfoWrapper.maxIntegerToNull(wifiInfo.getFrequency()));
		return wrapper;
	}

	public static CellIdentityWrapper fromSignalItem(final SignalItem signalItem) {
		final CellIdentityWrapper wrapper = new CellIdentityWrapper();
		wrapper.setRegistered(true);

		if (signalItem.lteRsrp != null) {
			wrapper.setCellInfoType(CellType.MOBILE_LTE);
		}
		else if (signalItem.wifiRssi != null) {
			wrapper.setCellInfoType(CellType.WLAN);
		}
		else if (signalItem.networkId != null) {
			wrapper.setCellInfoType(CellType.fromTelephonyNetworkTypeId(signalItem.networkId));
		}

		return wrapper;
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

	@Override
	public String toString() {
		return "CellIdentityWrapper{" +
				"cellInfoType=" + cellInfoType +
				", cellId=" + cellId +
				", physicalCellId=" + physicalCellId +
				", areaCode=" + areaCode +
				", scramblingCode=" + scramblingCode +
				", mcc=" + mcc +
				", mnc=" + mnc +
				", frequency=" + frequency +
				", isRegistered=" + isRegistered +
				'}';
	}
}