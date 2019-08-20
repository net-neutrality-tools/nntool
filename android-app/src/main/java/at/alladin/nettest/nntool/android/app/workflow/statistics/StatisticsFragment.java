package at.alladin.nettest.nntool.android.app.workflow.statistics;

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
public class StatisticsFragment extends ActionBarFragment {

    public static StatisticsFragment newInstance() {
        final StatisticsFragment fragment = new StatisticsFragment();
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
        return R.string.title_statistics;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_statistics_section;
    }
}
