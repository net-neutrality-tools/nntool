package at.alladin.nettest.nntool.android.app.workflow.measurement;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.view.BottomMeasurementResultSummaryView;
import at.alladin.nettest.nntool.android.app.view.MeasurementRecentResultSelectorView;
import at.alladin.nettest.nntool.android.app.workflow.main.TitleFragment;
import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class TitleWithRecentResultFragment extends TitleFragment implements ServiceConnection {

    private final static String TAG = TitleWithRecentResultFragment.class.getSimpleName();

    private BottomMeasurementResultSummaryView bottomMeasurementResultSummaryView;

    private MeasurementRecentResultSelectorView measurementRecentResultSelectorView;

    private MeasurementService measurementService;

    private SpeedMeasurementState recentSpeedMeasurementState;

    /**
     *
     * @return
     */
    public static TitleWithRecentResultFragment newInstance() {
        final TitleWithRecentResultFragment fragment = new TitleWithRecentResultFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_title_with_recent_result, container, false);

        final View startButton = v.findViewById(R.id.title_page_start_button);
        startButton.setOnClickListener(getNewOnClickListener());

        //and enable the result specific views
        measurementRecentResultSelectorView = v.findViewById(R.id.measurement_recent_result_selector_view);
        bottomMeasurementResultSummaryView = v.findViewById(R.id.bottom_measurement_result_summary_view);
        if (recentSpeedMeasurementState != null) {
            setMeasurementStateInBottomView();
        }
        return v;
    }

    private void setMeasurementStateInBottomView () {
        if (recentSpeedMeasurementState == null) {
            return;
        }

        if (recentSpeedMeasurementState.getPingMeasurement() != null) {
            bottomMeasurementResultSummaryView.setPingText(String.valueOf(recentSpeedMeasurementState.getPingMeasurement().getAverageMs()));
        } else {
            bottomMeasurementResultSummaryView.setPingText(getString(R.string.bottom_measurement_result_summary_view_ping_not_available));
        }

        if (recentSpeedMeasurementState.getDownloadMeasurement() != null) {
            bottomMeasurementResultSummaryView.setDownloadText(String.valueOf(recentSpeedMeasurementState.getDownloadMeasurement().getThroughputAvgBps()));
        } else {
            bottomMeasurementResultSummaryView.setDownloadText(getString(R.string.bottom_measurement_result_summary_view_download_not_available));
        }

        if (recentSpeedMeasurementState.getUploadMeasurement() != null) {
            bottomMeasurementResultSummaryView.setUploadText(String.valueOf(recentSpeedMeasurementState.getUploadMeasurement().getThroughputAvgBps()));
        } else {
            bottomMeasurementResultSummaryView.setUploadText(getString(R.string.bottom_measurement_result_summary_view_upload_not_available));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final Intent serviceIntent = new Intent(getContext(), MeasurementService.class);
        getContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        getContext().unbindService(this);
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "ON SERVICE CONNECTED");
        measurementService = ((MeasurementService.MeasurementServiceBinder) service).getService();
        recentSpeedMeasurementState = measurementService.getJniSpeedMeasurementClient().getSpeedMeasurementState();
        if (bottomMeasurementResultSummaryView != null) {
            setMeasurementStateInBottomView();
            bottomMeasurementResultSummaryView.invalidate();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "ON SERVICE DISCONNECTED");
        measurementService = null;
    }
}
