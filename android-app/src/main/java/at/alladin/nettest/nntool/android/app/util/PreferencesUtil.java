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

    private static SharedPreferences getDefaultPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
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
}
