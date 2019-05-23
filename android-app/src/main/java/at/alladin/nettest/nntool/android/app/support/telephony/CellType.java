package at.alladin.nettest.nntool.android.app.support.telephony;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public enum CellType {
    MOBILE_CDMA(TechnologyType.TECH_2G), /* MOBILE 2G */
    MOBILE_GSM(TechnologyType.TECH_2G), /* MOBILE 2G */
    MOBILE_WCDMA(TechnologyType.TECH_3G), /* MOBILE 3G */
    MOBILE_LTE(TechnologyType.TECH_4G), /* MOBILE 4G */
    WIFI(TechnologyType.TECH_WIFI); /* WIFI */

    private final TechnologyType technologyType;

    CellType(final TechnologyType technologyType) {
        this.technologyType = technologyType;
    }

    public TechnologyType getTechnologyType() {
        return technologyType;
    }
}
