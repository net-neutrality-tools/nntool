package at.alladin.nettest.nntool.android.app.workflow.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.RequestMeasurementTask;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementType;
import at.alladin.nntool.client.v2.task.TaskDesc;

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
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RequestMeasurementTask task = new RequestMeasurementTask(getContext(),
                        new OnTaskFinishedCallback<List<TaskDesc>>() {
                            @Override
                            public void onTaskFinished(List<TaskDesc> result) {
                                if (result != null && result.size() > 0) {
                                    final Bundle bundle = new Bundle();
                                    bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_DESK_LIST, (Serializable) result);
                                    ((MainActivity) getActivity()).startMeasurement(MeasurementType.QOS, bundle);
                                }
                            }
                        });

                task.execute();
            }
        });

        return v;
    }
}
