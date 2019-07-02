package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class PreferencesUtil {

    private final static String SETTING_TC_VERSION_ACCEPTED = "setting_nettest_tc_version_accepted";
    private final static String SETTING_AGENT_UUID = "setting_nettest_agent_uuid";
    private final static String SETTING_QOS_ENABLED = "setting_nettest_execute_qos_test";

    private static SharedPreferences getDefaultPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getTermsAndConditionsAcceptedVersion(final Context context) {
        return getDefaultPreferences(context).getInt(SETTING_TC_VERSION_ACCEPTED, -1);
    }

    public static boolean isTermsAndConditionsAccepted(final Context context, final int version) {
        return getDefaultPreferences(context).getInt(SETTING_TC_VERSION_ACCEPTED, -1) >= version;
    }

    public static void setTermsAndConditionsAccepted(final Context context, final int version) {
        getDefaultPreferences(context).edit().putInt(SETTING_TC_VERSION_ACCEPTED, version).commit();
    }

    public static void setAgentUuid(final Context context, final String uuid) {
        getDefaultPreferences(context).edit().putString(SETTING_AGENT_UUID, uuid).commit();
    }

    public static String getAgentUuid(final Context context) {
        return getDefaultPreferences(context).getString(SETTING_AGENT_UUID, null);
    }

    public static boolean isQoSEnabled(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTING_QOS_ENABLED, true);
    }

    public static void setQoSEnabled(final Context context, final boolean isQoSEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_QOS_ENABLED, isQoSEnabled).commit();
    }
}
