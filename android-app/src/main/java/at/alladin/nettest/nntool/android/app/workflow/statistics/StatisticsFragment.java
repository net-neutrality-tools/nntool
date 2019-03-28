package at.alladin.nettest.nntool.android.app.workflow.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class StatisticsFragment extends Fragment {

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
}
