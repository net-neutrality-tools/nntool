package at.alladin.nettest.nntool.android.app.workflow.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;

import java.util.Map;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.FunctionalityHelper;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private final static String TAG = SettingsFragment.class.getSimpleName();

    private final static String SINGLE_TEST_SELECTION_SCREEN_KEY = "single_test_selection_screen";
    private final static String QOS_TEST_SELECTION_CATEGORY_KEY = "selection_qos";

    private static Map<QoSMeasurementTypeDto, SettingsResponse.TranslatedQoSTypeInfo> qosTranslationInfo = null;
    private String rootKey;

    public static SettingsFragment newInstance() {
        final SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public static SettingsFragment newInstance(final String rootKey) {
        final SettingsFragment fragment = new SettingsFragment();
        if (rootKey != null) {
            fragment.setRootKey(rootKey);
        }
        return fragment;
    }

    @Override
    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_layout, SettingsFragment.newInstance(preferenceScreen.getKey()))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootKey == null) {
            ((MainActivity)getActivity()).updateActionBar(getString(R.string.title_settings));
        } else {
            switch (rootKey) {
                case SINGLE_TEST_SELECTION_SCREEN_KEY:
                    ((MainActivity)getActivity()).updateActionBar(getString(R.string.title_settings_test_selection), true);
                    break;
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        if (this.rootKey != null) {
            s = rootKey;
        }
        setPreferencesFromResource(R.xml.preferences, s);

        if (qosTranslationInfo == null || qosTranslationInfo.size() == 0) {
            qosTranslationInfo = PreferencesUtil.getQoSTypeInfo(getContext());
        }

        //if we are @ the test selection subscreen => populate the view w/the qos types
        if (SINGLE_TEST_SELECTION_SCREEN_KEY.equals(s) && qosTranslationInfo != null && qosTranslationInfo.size() > 0) {
            final PreferenceCategory qosCategory = (PreferenceCategory) findPreference(QOS_TEST_SELECTION_CATEGORY_KEY);
            if (qosCategory != null) {
                for (QoSMeasurementTypeDto value : QoSMeasurementTypeDto.values()) {
                    if (!FunctionalityHelper.isQoSTypeAvailable(value, getContext())) {
                        continue;
                    }
                    final SettingsResponse.TranslatedQoSTypeInfo translationInfo = qosTranslationInfo.get(value);
                    if (translationInfo == null) {
                        continue;
                    }
                    qosCategory.addPreference(createQoSSwitchPreference(qosCategory.getContext(), value, translationInfo));
                }
            }
        }
    }


    public String getRootKey() {
        return rootKey;
    }

    public void setRootKey(String rootKey) {
        this.rootKey = rootKey;
    }

    private SwitchPreferenceCompat createQoSSwitchPreference(final Context context, final QoSMeasurementTypeDto type, final SettingsResponse.TranslatedQoSTypeInfo translationInfo) {
        final SwitchPreferenceCompat ret = new SwitchPreferenceCompat(context);
        ret.setKey(PreferencesUtil.SETTING_QOS_TYPE_PREFERENCE_PREFIX + type.name());
        ret.setDefaultValue(true);
        ret.setTitle(translationInfo.getName());
        ret.setSummary(translationInfo.getDescription());
        return ret;
    }
}
