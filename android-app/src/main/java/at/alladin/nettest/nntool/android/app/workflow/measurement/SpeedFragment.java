package at.alladin.nettest.nntool.android.app.workflow.measurement;

import android.content.ComponentName;
import android.content.Context;
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.view.CanvasArcDoubleGaugeWithLabels;
import at.alladin.nettest.nntool.android.app.view.TopProgressBarView;
import at.alladin.nettest.nntool.android.app.workflow.measurement.jni.JniSpeedMeasurementClient;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.QoSTestEnum;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class SpeedFragment  extends Fragment implements ServiceConnection {

    private final static String TAG = "SPEED_FRAGMENT";

    private MeasurementService measurementService;

    private TopProgressBarView topProgressBarView;

    private CanvasArcDoubleGaugeWithLabels cadlabprogView;

    private AtomicBoolean sendingResults = new AtomicBoolean(false);

    public static SpeedFragment newInstance() {
        final SpeedFragment fragment = new SpeedFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_speed, container, false);
        topProgressBarView = view.findViewById(R.id.top_progress_bar_view);
        cadlabprogView = view.findViewById(R.id.canvas_arc_double_gauge_with_labels);
        if (measurementService != null) {
            Log.d(TAG, "assigned speed measurement state");
            cadlabprogView.setSpeedMeasurementState(measurementService.getJniSpeedMeasurementClient().getSpeedMeasurementState());
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //prevent back key press
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
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
        //handler.removeCallbacks(updateUiRunnable);
        getContext().unbindService(this);
        super.onPause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "ON SERVICE CONNECTED");
        measurementService = ((MeasurementService.MeasurementServiceBinder) service).getService();
        if (cadlabprogView != null) {
            Log.d(TAG, "assigned speed measurement state from onServiceConnected");
            cadlabprogView.setSpeedMeasurementState(measurementService.getJniSpeedMeasurementClient().getSpeedMeasurementState());
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


            if (!postResultRunnable) {
                handler.postDelayed(this, 50);
            }
            else {
                //handler.postDelayed(showResultsRunnable, 1000);
            }
        }
    };
}
