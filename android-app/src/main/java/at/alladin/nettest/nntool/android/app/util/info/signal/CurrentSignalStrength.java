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

package at.alladin.nettest.nntool.android.app.util.info.signal;

import at.alladin.nettest.nntool.android.app.support.telephony.CellInfoWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.CellSignalStrengthWrapper;
import at.alladin.nettest.nntool.android.app.support.telephony.CellType;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CurrentSignalStrength {

    public enum SignalType {
        UNKNOWN,
        MOBILE,
        WIFI,
        RSRP;

        public static SignalType fromCellType(final CellType cellType) {
            switch (cellType) {
                case MOBILE_LTE:
                    return RSRP;

                case MOBILE_CDMA:
                case MOBILE_GSM:
                case MOBILE_WCDMA:
                    return MOBILE;

                case WLAN:
                    return WIFI;
            }

            return UNKNOWN;
        }

        public static SignalType fromCellInfoWrapper(final CellInfoWrapper wrapper) {
            if (wrapper.getCellIdentityWrapper() != null && wrapper.getCellIdentityWrapper().getCellInfoType() != null) {
                return fromCellType(wrapper.getCellIdentityWrapper().getCellInfoType());
            }

            return UNKNOWN;
        }
    }

    final SignalType signalType;
    final Integer signalDbm;
    final Integer lteRsrq;
    final CellInfoWrapper cellInfoWrapper;

    public CurrentSignalStrength(final SignalType signalType, final Integer signalDbm,
                                 final CellInfoWrapper wrapper) {
        this(signalType, signalDbm, null, wrapper);
    }

    public CurrentSignalStrength(final SignalType signalType, final Integer signalDbm,
                                 final Integer lteRsrq, final CellInfoWrapper wrapper) {
        this.signalType = signalType;
        this.signalDbm = signalDbm;
        this.lteRsrq = lteRsrq;
        this.cellInfoWrapper = wrapper;
    }

    public static CurrentSignalStrength fromCellInfoWrapper(final CellInfoWrapper wrapper) {
        final SignalType signalType = SignalType.fromCellInfoWrapper(wrapper);
        Integer signal = null;
        Integer signalRsrq = null;

        final CellSignalStrengthWrapper cssw = wrapper.getCellSignalStrengthWrapper();
        if (cssw != null) {
            switch (signalType) {
                case WIFI:
                    signal = cssw.getWifiRssi();
                    break;
                case RSRP:
                    signal = cssw.getLteRsrp();
                    signalRsrq = cssw.getLteRsrq();
                    break;
                case MOBILE:
                    signal = cssw.getSignalStrength();
                    break;
            }
        }

        final CurrentSignalStrength css = new CurrentSignalStrength(signalType, signal, signalRsrq, wrapper);
        return css;
    }

    public SignalType getSignalType() {
        return signalType;
    }

    public Integer getSignalDbm() {
        return signalDbm;
    }

    public Integer getLteRsrq() {
        return lteRsrq;
    }

    public CellInfoWrapper getCellInfoWrapper() {
        return cellInfoWrapper;
    }

    @Override
    public String toString() {
        return "CurrentSignalStrength{" +
                "signalType=" + signalType +
                ", signalDbm=" + signalDbm +
                ", lteRsrq=" + lteRsrq +
                ", cellInfoWrapper=" + cellInfoWrapper +
                '}';
    }
}
