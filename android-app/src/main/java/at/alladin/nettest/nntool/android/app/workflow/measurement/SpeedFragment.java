package at.alladin.nettest.nntool.android.app.workflow.measurement;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.OnTaskFinishedCallback;
import at.alladin.nettest.nntool.android.app.async.SendReportTask;
import at.alladin.nettest.nntool.android.app.util.AlertDialogUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.RequestUtil;
import at.alladin.nettest.nntool.android.app.view.AlladinTextView;
import at.alladin.nettest.nntool.android.app.view.BottomMeasurementResultSummaryView;
import at.alladin.nettest.nntool.android.app.view.CanvasArcDoubleGaugeWithLabels;
import at.alladin.nettest.nntool.android.app.view.TopProgressBarView;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SpeedFragment extends ActionBarFragment implements ServiceConnection {

    private final static String TAG = "SPEED_FRAGMENT";

    private final static float baseProgressPercentage = 0.25f;

    private MeasurementService measurementService;

    private SpeedMeasurementState speedMeasurementState;

    private TopProgressBarView topProgressBarView;

    private BottomMeasurementResultSummaryView bottomMeasurementResultSummary;

    private CanvasArcDoubleGaugeWithLabels cadlabprogView;

    private AlladinTextView gaugePhaseIndicator;

    private AtomicBoolean sendingResults = new AtomicBoolean(false);

    private boolean isQoSEnabled = true;

    public static SpeedFragment newInstance() {
        final SpeedFragment fragment = new SpeedFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_speed, container, false);
        topProgressBarView = view.findViewById(R.id.top_progress_bar_view);
        topProgressBarView.setLeftText("0 %");
        topProgressBarView.setRightIcon(getResources().getString(R.string.ifont_hourglass));
        topProgressBarView.setVisibility(View.VISIBLE);
        cadlabprogView = view.findViewById(R.id.canvas_arc_double_gauge_with_labels);
        bottomMeasurementResultSummary = view.findViewById(R.id.bottom_measurement_result_summary_view);
        bottomMeasurementResultSummary.setVisibility(View.VISIBLE);
        gaugePhaseIndicator = view.findViewById(R.id.gauge_phase_indicator);

        isQoSEnabled = PreferencesUtil.isQoSEnabled(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //prevent back key press
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {

            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                AlertDialogUtil.showCancelDialog(getContext(), R.string.alert_cancel_measurement_title, R.string.alert_cancel_measurement_content,
                        (dialog, which) -> {
                            measurementService.stopSpeedMeasurement();
                            ((MainActivity) getActivity()).navigateTo(WorkflowTarget.TITLE);
                            dialog.cancel();
                        },
                            (dialog, which) -> dialog.cancel()
                        );
                return true;
            }

            return false;
        });

        final Intent serviceIntent = new Intent(getContext(), MeasurementService.class);
        getContext().bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);

        if (!sendingResults.get()) {
            //only if we are not currently sending any results
            handler.post(updateUiRunnable);
        }
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(updateUiRunnable);
        getContext().unbindService(this);
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "ON SERVICE CONNECTED");
        measurementService = ((MeasurementService.MeasurementServiceBinder) service).getService();
        if (measurementService != null) {
            this.speedMeasurementState = measurementService.getJniSpeedMeasurementClient().getSpeedMeasurementState();
            measurementService.setOnMeasurementErrorListener((ex) ->
            {
                getActivity().runOnUiThread(() -> {
                    AlertDialogUtil.showAlertDialog(getContext(), R.string.alert_error_during_measurement_title, R.string.alert_error_during_measurement_content,
                            (dialog, which) -> ((MainActivity) getActivity()).navigateTo(WorkflowTarget.TITLE));
                });
            });
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "ON SERVICE DISCONNECTED");
        measurementService = null;
    }

    final private Handler handler = new Handler();

    final Runnable updateUiRunnable = new Runnable() {
        @Override
        public void run() {
            boolean postResultRunnable = false;

            if (speedMeasurementState != null) {

                SpeedMeasurementState.MeasurementPhase currentPhase = speedMeasurementState.getMeasurementPhase();
                float progress = 0;
                double speed = 0;
                long pingAverage = 0;
                switch (currentPhase) {
                    case INIT:
                        break;
                    case PING:
                        pingAverage = speedMeasurementState.getPingMeasurement().getAverageMs();
                        progress = baseProgressPercentage * 1;
                        gaugePhaseIndicator.setText(getResources().getString(R.string.ifont_ping));
                        topProgressBarView.setRightIcon(getResources().getString(R.string.ifont_ping));
                        break;
                    case DOWNLOAD:
                        speed = speedMeasurementState.getDownloadMeasurement().getThroughputAvgBps() / 1e6;
                        progress = baseProgressPercentage * 2;
                        gaugePhaseIndicator.setText(getResources().getString(R.string.ifont_down));
                        topProgressBarView.setRightIcon(getResources().getString(R.string.ifont_down));
                        break;
                    case UPLOAD:
                        speed = speedMeasurementState.getUploadMeasurement().getThroughputAvgBps() / 1e6;
                        progress = baseProgressPercentage * 3;
                        gaugePhaseIndicator.setText(getResources().getString(R.string.ifont_up));
                        topProgressBarView.setRightIcon(getResources().getString(R.string.ifont_up));
                        break;
                    case END:
                        progress = baseProgressPercentage * 4;
                        postResultRunnable = true;
                        break;
                }
                progress += 0.25 * speedMeasurementState.getProgress();
                progress = Math.min(1, progress);

                if (cadlabprogView != null) {
                    cadlabprogView.setProgressValue(progress);
                    cadlabprogView.setSpeedValue(speed);
                    cadlabprogView.invalidate();
                }

                if (topProgressBarView != null) {
                    //the upper left text needs to display the correct number (depending on whether QoS is enabled or not
                    topProgressBarView.setLeftText(String.format(Locale.getDefault(), "%d %%", Math.min(100, (int)
                            (isQoSEnabled ? progress * 100 * 0.8 : progress * 100))));
                    if (currentPhase == SpeedMeasurementState.MeasurementPhase.PING) {
                        topProgressBarView.setRightText(String.format(Locale.getDefault(), "%d " + getResources().getString(R.string.top_progress_bar_view_ping_unit), pingAverage));
                    } else {
                        topProgressBarView.setRightText(String.format(Locale.getDefault(), "%.2f " + getResources().getString(R.string.top_progress_bar_view_speed_unit), speed));
                    }
                }

                if (bottomMeasurementResultSummary != null) {
                    switch (currentPhase) {
                        case END:
                        case UPLOAD:
                            bottomMeasurementResultSummary.setUploadText(String.format(Locale.getDefault(), "%.2f", speedMeasurementState.getUploadMeasurement().getThroughputAvgBps() / 1e6));
                        case DOWNLOAD:
                            bottomMeasurementResultSummary.setDownloadText(String.format(Locale.getDefault(), "%.2f", speedMeasurementState.getDownloadMeasurement().getThroughputAvgBps() / 1e6));
                        case PING:
                            bottomMeasurementResultSummary.setPingText(String.valueOf(speedMeasurementState.getPingMeasurement().getAverageMs()));
                            break;
                    }
                }
            }

            if (!postResultRunnable) {
                handler.postDelayed(this, 50);
            }
            else {
                Log.d(TAG, "post result runnable started");
                handler.postDelayed(showResultsRunnable, 1000);
            }
        }
    };

    final Runnable showResultsRunnable = new Runnable() {
        @Override
        public void run() {
            final MainActivity activity = (MainActivity) getActivity();
            if (measurementService.hasFollowUpAction()) {
                measurementService.executeFollowUpAction(activity);
            } else {
                measurementService.sendResults(activity, sendingResults);
            }
        }
    };

    @Override
    public Integer getTitleStringId() {
        return R.string.app_name;
    }

    @Override
    public boolean showHelpButton() {
        return false;
    }
}
