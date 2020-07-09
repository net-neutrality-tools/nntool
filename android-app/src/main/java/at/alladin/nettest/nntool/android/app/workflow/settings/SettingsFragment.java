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

package at.alladin.nettest.nntool.android.app.workflow.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import at.alladin.nettest.nntool.android.app.BuildConfig;
import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.DisassociateAgentTask;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.AlertDialogUtil;
import at.alladin.nettest.nntool.android.app.util.FunctionalityHelper;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.nntool.android.app.workflow.help.HelpFragment;
import at.alladin.nettest.nntool.android.app.workflow.main.WorkflowTitleParameter;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private final static String TAG = SettingsFragment.class.getSimpleName();

    private final static String ROOT_PREFERENCE_SCREEN_KEY = "root_preference_screen";
    private final static String SINGLE_TEST_SELECTION_SCREEN_KEY = "single_test_selection_screen";

    private final static String QOS_TEST_SELECTION_CATEGORY_KEY = "selection_qos";
    private final static String DISASSOCIATE_USER_KEY = "settings_disassociate_user";
    private final static String PREFERENCE_COMMIT_HASH = "setting_current_commit_hash";

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
        setHasOptionsMenu(true);
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
        } else if (ROOT_PREFERENCE_SCREEN_KEY.equals(s) || s == null) {
            final Preference disassociatePreference = findPreference(DISASSOCIATE_USER_KEY);
            if (disassociatePreference != null) {
                disassociatePreference.setOnPreferenceClickListener(preference -> {
                    Log.d(TAG, "opening deletion dialog");
                    AlertDialogUtil.showCancelDialog(getContext(), R.string.preference_disassociate_user_warning_title, R.string.preference_disassociate_user_warning_content,
                            (dialog, which) -> {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    Log.d(TAG, "Starting deletion of user");
                                    DisassociateAgentTask task = new DisassociateAgentTask(getContext(), result -> {
                                        PreferencesUtil.setAgentUuid(getContext(), null);
                                        PreferencesUtil.setTermsAndConditionsAccepted(getContext(), null);
                                        final WorkflowTitleParameter param = new WorkflowTitleParameter();
                                        param.setShowTermsAndConditionsOnLoad(true);
                                        ((MainActivity) getActivity()).navigateTo(WorkflowTarget.TITLE, param);
                                    });
                                    final BlockingProgressDialog progressDialog = new BlockingProgressDialog.Builder(getContext()).setCancelable(false).setMessage(R.string.preference_disassociate_user_progress_dialog).build();
                                    progressDialog.show();
                                    task.execute();
                                    try {
                                        task.get();
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    progressDialog.dismiss();
                                }
                            }, null);
                    return true;
                });
            }

            final Preference commitPreference = findPreference(PREFERENCE_COMMIT_HASH);
            if (commitPreference != null) {
                commitPreference.setSummary(BuildConfig.GIT_VERSION_STRING);
                commitPreference.setOnPreferenceClickListener(preference -> {
                    final ClipboardManager manager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (manager != null) {
                        ClipData clip = ClipData.newPlainText(getString(R.string.preference_current_commit_hash), commitPreference.getSummary());
                        manager.setPrimaryClip(clip);
                        Toast.makeText(getContext(), getString(R.string.preference_current_commit_hash_copy_toast), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                });
            }
        }
    }


    public String getRootKey() {
        return rootKey;
    }

    public void setRootKey(String rootKey) {
        this.rootKey = rootKey;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Integer helpSectionStringId = getHelpSectionStringId();
        if (R.id.action_bar_show_help_action == item.getItemId()) {
            HelpFragment.showHelpFragment(getActivity(), helpSectionStringId, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Integer getTitleStringId() {
        return R.string.title_settings;
    }

    public Integer getHelpSectionStringId() {
        return R.string.help_link_settings_section;
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
