package at.alladin.nettest.nntool.android.app.workflow.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class TitleFragment extends Fragment {

    /**
     *
     * @return
     */
    public static TitleFragment newInstance() {
        final TitleFragment fragment = new TitleFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_title, container, false);

        final View startButton = v.findViewById(R.id.title_page_start_button);
        startButton.setOnClickListener(l -> ((MainActivity) getActivity()).startMeasurement(MeasurementType.QOS));

        return v;
    }
}
