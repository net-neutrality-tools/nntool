package at.alladin.nettest.nntool.android.app.workflow.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.RequestMeasurementTask;
import at.alladin.nettest.nntool.android.app.util.LmapUtil;
import at.alladin.nettest.nntool.android.app.workflow.measurement.MeasurementService;
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
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RequestMeasurementTask task = new RequestMeasurementTask(getContext(),
                        new OnTaskFinishedCallback<LmapUtil.LmapTaskWrapper>() {
                            @Override
                            public void onTaskFinished(LmapUtil.LmapTaskWrapper result) {
                                if (result != null && result.getTaskDescList() != null && result.getTaskDescList().size() > 0) {
                                    final Bundle bundle = new Bundle();
                                    bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_DESC_LIST,
                                            (Serializable) result.getTaskDescList());
                                    bundle.putSerializable(MeasurementService.EXTRAS_KEY_QOS_TASK_COLLECTOR_URL,
                                            result.getCollectorUrl());
                                    bundle.putSerializable(MeasurementService.EXTRAS_KEY_SPEED_TASK_COLLECTOR_URL,
                                            result.getSpeedCollectorUrl());
                                    bundle.putSerializable(MeasurementService.EXTRAS_KEY_SPEED_TASK_DESC,
                                            result.getSpeedTaskDesc());
                                    ((MainActivity) getActivity()).startMeasurement(MeasurementType.SPEED, bundle);
                                }
                            }
                        });

                task.execute();
                /*
                final Bundle bundle = new Bundle();
                ((MainActivity) getActivity()).startMeasurement(MeasurementType.SPEED, bundle);
                */
            }
        });

        return v;
    }
}
