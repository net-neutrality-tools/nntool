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

package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nntool.shared.qos.QosMeasurementType;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class PreferencesUtil {

    private final static String USER_AGENT_IDENTIFIER = "nntool/1.0";

    //the prefix to be applied to all single qos types (TCP, UDP, ...) for storage in the SharedPreferences
    public final static String SETTING_QOS_TYPE_PREFERENCE_PREFIX = "setting_qos_execute_";

    private final static String SETTING_TC_VERSION_ACCEPTED = "setting_nettest_tc_version_accepted";
    private final static String SETTING_AGENT_UUID = "setting_nettest_agent_uuid";

    private final static String SETTING_QOS_ENABLED = "setting_nettest_execute_qos_test";
    private final static String SETTING_SPEED_ENABLED = "setting_nettest_execute_speed_test";
    private final static String SETTING_PING_ENABLED = "setting_nettest_execute_ping_test";
    private final static String SETTING_DOWNLOAD_ENABLED = "setting_nettest_execute_download_test";
    private final static String SETTING_UPLOAD_ENABLED = "setting_nettest_execute_upload_test";

    private final static String SETTINGS_QOS_TYPE_INFORMATION_MAP= "settings_qos_type_information_map";

    private final static String SETTINGS_IPV4_ONLY = "setting_ipv4_only";

    private final static String SETTINGS_URLS_RESULT_SERVICE_BASE_URL = "settings_urls_result_service_base_url";
    private final static String SETTINGS_URLS_STATISTICS_SERVICE_BASE_URL = "settings_urls_statistics_service_base_url";
    private final static String SETTINGS_URLS_MAP_SERVICE_BASE_URL = "settings_urls_map_service_base_url";

    private static SharedPreferences getDefaultPreferences(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static int getTermsAndConditionsAcceptedVersion(final Context context) {
        return getDefaultPreferences(context).getInt(SETTING_TC_VERSION_ACCEPTED, -1);
    }

    public static boolean isTermsAndConditionsAccepted(final Context context, final int version) {
        return getDefaultPreferences(context).getInt(SETTING_TC_VERSION_ACCEPTED, -1) >= version;
    }

    public static void setTermsAndConditionsAccepted(final Context context, final Integer version) {
        if (version != null) {
            getDefaultPreferences(context).edit().putInt(SETTING_TC_VERSION_ACCEPTED, version).commit();
        } else {
            getDefaultPreferences(context).edit().remove(SETTING_TC_VERSION_ACCEPTED).commit();
        }
    }

    public static void setAgentUuid(final Context context, final String uuid) {
        if (uuid != null) {
            getDefaultPreferences(context).edit().putString(SETTING_AGENT_UUID, uuid).commit();
        }
        else{
            getDefaultPreferences(context).edit().remove(SETTING_AGENT_UUID).commit();
        }
    }

    public static String getAgentUuid(final Context context) {
        if (context.getResources().getBoolean(R.bool.debug_functionality_override_agent_uuid)) {
            final String overrideUuid = context.getResources().getString(R.string.debug_functionality_agent_uuid);
            try {
                //check if uuid is valid
                UUID.fromString(overrideUuid);
                return overrideUuid;
            }
            catch (final IllegalArgumentException e) {
                //invalid uuid
            }
        }

        return getDefaultPreferences(context).getString(SETTING_AGENT_UUID, null);
    }

    public static boolean isQoSEnabled(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTING_QOS_ENABLED, true);
    }

    public static void setQoSEnabled(final Context context, final boolean isQoSEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_QOS_ENABLED, isQoSEnabled).commit();
    }

    public static boolean isSpeedEnabled(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTING_SPEED_ENABLED, true);
    }

    public static void setSpeedEnabled(final Context context, final boolean isSpeedEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_SPEED_ENABLED, isSpeedEnabled).commit();
    }

    public static boolean isPingEnabled(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTING_PING_ENABLED, true);
    }

    public static void setPingEnabled(final Context context, final boolean isPingEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_PING_ENABLED, isPingEnabled).commit();
    }

    public static boolean isDownloadEnabled(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTING_DOWNLOAD_ENABLED, true);
    }

    public static void setDownloadEnabled(final Context context, final boolean isDownloadEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_DOWNLOAD_ENABLED, isDownloadEnabled).commit();
    }

    public static boolean isUploadEnabled(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTING_UPLOAD_ENABLED, true);
    }

    public static void setUploadEnabled(final Context context, final boolean isUploadEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_UPLOAD_ENABLED, isUploadEnabled).commit();
    }

    public static void setQoSTypeInfo(final Context context, final Map<QoSMeasurementTypeDto, SettingsResponse.TranslatedQoSTypeInfo> qoSTypeInfoMap) {
        try {
            getDefaultPreferences(context)
                    .edit()
                    .putString(SETTINGS_QOS_TYPE_INFORMATION_MAP, ObjectMapperUtil.createBasicObjectMapper().writeValueAsString(qoSTypeInfoMap))
                    .apply();
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<QoSMeasurementTypeDto, SettingsResponse.TranslatedQoSTypeInfo> getQoSTypeInfo(final Context context) {
        try {
            return ObjectMapperUtil.createBasicObjectMapper().readValue(
                    getDefaultPreferences(context)
                            .getString(SETTINGS_QOS_TYPE_INFORMATION_MAP, ""),
                    new TypeReference<LinkedHashMap<QoSMeasurementTypeDto, SettingsResponse.TranslatedQoSTypeInfo>>() {
                    }
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new LinkedHashMap<>();
    }

    public static void setSettingsUrls(final Context context, final SettingsResponse.Urls urls) {
        getDefaultPreferences(context)
                .edit()
                .putString(SETTINGS_URLS_RESULT_SERVICE_BASE_URL, urls.getResultService())
                .putString(SETTINGS_URLS_MAP_SERVICE_BASE_URL, urls.getMapService())
                .putString(SETTINGS_URLS_STATISTICS_SERVICE_BASE_URL, urls.getStatisticService())
                .commit();
    }

    public static String getResultServiceUrl(final Context context) {
        return getDefaultPreferences(context).getString(SETTINGS_URLS_RESULT_SERVICE_BASE_URL, null);
    }

    public static String getMapServiceUrl(final Context context) {
        return getDefaultPreferences(context).getString(SETTINGS_URLS_MAP_SERVICE_BASE_URL, null);
    }

    public static String getStatisticsServiceUrl(final Context context) {
        return getDefaultPreferences(context).getString(SETTINGS_URLS_STATISTICS_SERVICE_BASE_URL, null);
    }

    public static boolean isQoSTypeEnabled(final Context context, final QosMeasurementType type) {
        return getDefaultPreferences(context).getBoolean(SETTING_QOS_TYPE_PREFERENCE_PREFIX + type.name(), true);
    }

    public static void setQoSTypeEnabled(final Context context, final QosMeasurementType type, final boolean isEnabled) {
        getDefaultPreferences(context).edit().putBoolean(SETTING_QOS_TYPE_PREFERENCE_PREFIX + type.name(), isEnabled).commit();
    }

    public static boolean isForceIpv4(final Context context) {
        return getDefaultPreferences(context).getBoolean(SETTINGS_IPV4_ONLY, false);
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getUserAgentString(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return USER_AGENT_IDENTIFIER + " (Android; " + Locale.getDefault().toString() + "; API" + Build.VERSION.SDK_INT + ") BEREC_nntool/" + pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
