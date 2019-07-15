package at.alladin.nettest.nntool.android.app.workflow.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class SettingsFragment extends ActionBarFragment {

    public static SettingsFragment newInstance() {
        final SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.settings_fragment, container, false);

        return v;
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.title_settings;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_settings_section;
    }
}
