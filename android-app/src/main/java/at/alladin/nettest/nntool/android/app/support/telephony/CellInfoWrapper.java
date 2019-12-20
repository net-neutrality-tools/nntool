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

import android.net.wifi.WifiInfo;
import android.telephony.CellInfo;

import at.alladin.nettest.nntool.android.app.util.info.signal.CurrentSignalStrength;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CellInfoWrapper {

    public CellIdentityWrapper cellIdentityWrapper;

    public CellSignalStrengthWrapper cellSignalStrengthWrapper;

    public CellInfoWrapper() { }

    public CellInfoWrapper(CellIdentityWrapper cellIdentityWrapper, CellSignalStrengthWrapper cellSignalStrengthWrapper) {
        this.cellIdentityWrapper = cellIdentityWrapper;
        this.cellSignalStrengthWrapper = cellSignalStrengthWrapper;
    }

    public static CellInfoWrapper fromCellInfo(final CellInfo cellInfo) {
        final CellInfoWrapper wrapper = new CellInfoWrapper(CellIdentityWrapper.fromCellInfo(cellInfo),
                CellSignalStrengthWrapper.fromCellInfo(cellInfo));
        //wrapper.getCellSignalStrengthWrapper().setNetworkId(networkId);
        return wrapper;
    }

    public static CellInfoWrapper fromWifiInfo(final WifiInfo wifiInfo) {
        final CellInfoWrapper wrapper = new CellInfoWrapper(CellIdentityWrapper.fromWifiInfo(wifiInfo),
                CellSignalStrengthWrapper.fromWifiInfo(wifiInfo));
        return wrapper;
    }

    public static CellInfoWrapper fromSignalItem(final SignalItem signalItem) {
        final CellInfoWrapper wrapper = new CellInfoWrapper(CellIdentityWrapper.fromSignalItem(signalItem),
                CellSignalStrengthWrapper.fromSignalItem(signalItem));
        return wrapper;
    }

    public static Integer maxIntegerToNull(final Integer i) {
        return i == null ? null : (i != Integer.MAX_VALUE) ? i : null;
    }

    public static Integer minIntegerToNull(final Integer i) {
        return i == null ? null : (i != Integer.MIN_VALUE) ? i : null;
    }

    public static Integer minAndMaxIntegerToNull(final Integer i) {
        return minIntegerToNull(maxIntegerToNull(i));
    }

    public CellIdentityWrapper getCellIdentityWrapper() {
        return cellIdentityWrapper;
    }

    public void setCellIdentityWrapper(CellIdentityWrapper cellIdentityWrapper) {
        this.cellIdentityWrapper = cellIdentityWrapper;
    }

    public CellSignalStrengthWrapper getCellSignalStrengthWrapper() {
        return cellSignalStrengthWrapper;
    }

    public void setCellSignalStrengthWrapper(CellSignalStrengthWrapper cellSignalStrengthWrapper) {
        this.cellSignalStrengthWrapper = cellSignalStrengthWrapper;
    }

    @Override
    public String toString() {
        return "CellInfoWrapper{" +
                "cellIdentityWrapper=" + cellIdentityWrapper +
                ", cellSignalStrengthWrapper=" + cellSignalStrengthWrapper +
                '}';
    }
}
