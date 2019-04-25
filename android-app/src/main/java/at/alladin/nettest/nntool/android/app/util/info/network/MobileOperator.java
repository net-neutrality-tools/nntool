package at.alladin.nettest.nntool.android.app.util.info.network;

import android.telephony.TelephonyManager;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public final class MobileOperator implements OperatorInfo {

    private final String networkOperatorName;
    private final String networkOperator;
    private final String networkCountryCode;
    private final String simOperator;
    private final String simOpetatorName;
    private final String simCountryCode;

    public MobileOperator(final TelephonyManager telephonyManager) {
        final String tNetworkOperator = telephonyManager.getNetworkOperator();
        if (tNetworkOperator != null && tNetworkOperator.length() >= 5) {
            this.networkOperator = String.format("%s-%s", tNetworkOperator.substring(0, 3), tNetworkOperator.substring(3));
        } else {
            this.networkOperator = "";
        }

        this.networkOperatorName = telephonyManager.getNetworkOperatorName();
        this.networkCountryCode = telephonyManager.getNetworkCountryIso();

        final String tSimOperator = telephonyManager.getSimOperator();
        if (tSimOperator != null && tSimOperator.length() >= 5) {
            this.simOperator = String.format("%s-%s", tSimOperator.substring(0, 3), tSimOperator.substring(3));
        } else {
            this.simOperator = "";
        }
        this.simOpetatorName = telephonyManager.getSimOperatorName();
        this.simCountryCode = telephonyManager.getSimCountryIso();
    }

    /**
     * Returns the alphabetic name of current registered operator.
     *
     * @return
     */
    public String getNetworkOperatorName() {
        return networkOperatorName;
    }

    /**
     * Returns the numeric name (MCC+MNC) of current registered operator.
     *
     * @return
     */
    public String getNetworkOperator() {
        return networkOperator;
    }

    /**
     * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.
     *
     * @return
     */
    public String getSimOperator() {
        return simOperator;
    }

    /**
     * Returns the Service Provider Name (SPN).
     *
     * @return
     */
    public String getSimOpetatorName() {
        return simOpetatorName;
    }

    /**
     * Returns the ISO country code equivalent for the SIM provider's country code.
     *
     * @return
     */
    public String getSimCountryCode() {
        return simCountryCode;
    }

    /**
     * Returns the ISO country code equivalent of the MCC (Mobile Country Code) of the current registered operator or the cell nearby, if available.
     *
     * @return
     */
    public String getNetworkCountryCode() {
        return networkCountryCode;
    }

    @Override
    public String getOperatorName() {
        if (networkOperator.length() == 0 && networkOperatorName.length() == 0) {
            return "-";
        } else if (networkOperator.length() == 0) {
            return networkOperatorName;
        } else if (networkOperatorName.length() == 0) {
            return networkOperator;
        }

        return String.format("%s (%s)", networkOperatorName, networkOperator);
    }

    @Override
    public String toString() {
        return "MobileOperator{" +
                "networkOperatorName='" + networkOperatorName + '\'' +
                ", networkOperator='" + networkOperator + '\'' +
                ", networkCountryCode='" + networkCountryCode + '\'' +
                ", simOperator='" + simOperator + '\'' +
                ", simOpetatorName='" + simOpetatorName + '\'' +
                ", simCountryCode='" + simCountryCode + '\'' +
                '}';
    }
}
