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

import android.telephony.TelephonyManager;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public enum CellType {
    MOBILE_CDMA(TechnologyType.TECH_2G), /* MOBILE 2G */
    MOBILE_GSM(TechnologyType.TECH_2G), /* MOBILE 2G */
    MOBILE_WCDMA(TechnologyType.TECH_3G), /* MOBILE 3G */
    MOBILE_LTE(TechnologyType.TECH_4G), /* MOBILE 4G */
    MOBILE_ANY(TechnologyType.TECH_MOBILE_ANY), /* unknown mobile */
    WLAN(TechnologyType.TECH_WLAN),  /* WLAN */
    UNKNOWN(TechnologyType.UNKNOWN); /* UNKNOWN */

    private final TechnologyType technologyType;

    CellType(final TechnologyType technologyType) {
        this.technologyType = technologyType;
    }

    public TechnologyType getTechnologyType() {
        return technologyType;
    }

    public static CellType fromTelephonyNetworkTypeId(final int typeId) {
        final NetworkFamilyEnum networkFamily = NetworkFamilyEnum.getFamilyByNetworkId(typeId);
        if (networkFamily != null && networkFamily.getCellType() != null) {
            return networkFamily.getCellType();
        }

        return null;
    }

    private enum NetworkFamilyEnum {
        LAN("LAN"),
        ETHERNET("ETHERNET"),
        BLUETOOTH("BLUETOOTH"),
        WLAN("WLAN", "WLAN", CellType.WLAN),
        _1xRTT("1xRTT","2G"),
        _2G3G("2G/3G"),
        _3G4G("3G/4G"),
        _2G4G("2G/4G"),
        _2G3G4G("2G/3G/4G"),
        CLI("CLI"),
        CELLULAR_ANY("MOBILE","CELLULAR_ANY", MOBILE_ANY),
        GSM("GSM","2G", MOBILE_GSM),
        EDGE("EDGE","2G", MOBILE_GSM),
        UMTS("UMTS","3G", MOBILE_WCDMA),
        CDMA("CDMA","2G", MOBILE_CDMA),
        EVDO_0("EVDO_0","2G", MOBILE_CDMA),
        EVDO_A("EVDO_A","2G", MOBILE_CDMA),
        HSDPA("HSDPA","3G", MOBILE_WCDMA),
        HSUPA("HSUPA","3G", MOBILE_WCDMA),
        HSPA("HSPA","3G", MOBILE_WCDMA),
        IDEN("IDEN","2G", MOBILE_GSM),
        EVDO_B("EVDO_B","2G", MOBILE_CDMA),
        LTE("LTE","4G", MOBILE_LTE),
        EHRPD("EHRPD","2G", MOBILE_CDMA),
        HSPA_PLUS("HSPA+","3G", MOBILE_WCDMA),
        UNKNOWN("UNKNOWN");

        protected final String networkId;
        protected final String networkFamily;
        protected final CellType cellType;

        NetworkFamilyEnum(String networkId, String family) {
            this(networkId, family, null);
        }

        NetworkFamilyEnum(String networkId, String family, final CellType cellType) {
            this.networkFamily = family;
            this.networkId = networkId;
            this.cellType = cellType;
        }

        NetworkFamilyEnum(String family) {
            this(family, family);
        }

        public String getNetworkId() {
            return networkId;
        }

        public String getNetworkFamily() {
            return networkFamily;
        }

        public CellType getCellType() {
            return cellType;
        }

        public static NetworkFamilyEnum getFamilyByNetworkId(final int networkId) {
            final String networkTypeName = getNetworkTypeName(networkId);
            return getFamilyByNetworkIdName(networkTypeName);
        }

        public static NetworkFamilyEnum getFamilyByNetworkIdName(final String networkId) {
            for (NetworkFamilyEnum item : NetworkFamilyEnum.values()) {
                if (item.getNetworkId().equals(networkId)) {
                    return item;
                }
            }

            return UNKNOWN;
        }

        private static String getNetworkTypeName(final int type)
        {
            switch (type)
            {
                case 1:
                    return "GSM";
                case 2:
                    return "EDGE";
                case 3:
                    return "UMTS";
                case 4:
                    return "CDMA";
                case 5:
                    return "EVDO_0";
                case 6:
                    return "EVDO_A";
                case 7:
                    return "1xRTT";
                case 8:
                    return "HSDPA";
                case 9:
                    return "HSUPA";
                case 10:
                    return "HSPA";
                case 11:
                    return "IDEN";
                case 12:
                    return "EVDO_B";
                case 13:
                    return "LTE";
                case 14:
                    return "EHRPD";
                case 15:
                    return "HSPA+";
                case 98:
                    return "LAN";
                case 99:
                    return "WLAN";
                case 106:
                    return "ETHERNET";
                case 107:
                    return "BLUETOOTH";
                default:
                    return "UNKNOWN";
            }
        }
    }

}
