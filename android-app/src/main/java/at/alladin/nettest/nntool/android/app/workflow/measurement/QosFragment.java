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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.AlertDialogUtil;
import at.alladin.nettest.nntool.android.app.view.TopProgressBarView;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowTarget;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nntool.shared.qos.QosMeasurementType;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.QoSTestEnum;
import at.alladin.nntool.client.v2.task.result.QoSResultCollector;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class QosFragment extends ActionBarFragment implements ServiceConnection {
    private final static String TAG = "QOS_FRAGMENT";

    private MeasurementService measurementService;

    private QosProgressView qosProgressView;

    private TopProgressBarView topProgressBarView;

    private AtomicBoolean sendingResults = new AtomicBoolean(false);

    private boolean isSpeedEnabled = true;

    public static QosFragment newInstance(final WorkflowParameter parameter) {
        final QosFragment fragment = new QosFragment();
        if (parameter instanceof WorkflowMeasurementParameter) {
            fragment.setSpeedEnabled(((WorkflowMeasurementParameter)parameter).isSpeedEnabled());
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Method onCreateView
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_qos, container, false);
        qosProgressView = view.findViewById(R.id.qos_progress_view);
        topProgressBarView = view.findViewById(R.id.top_progress_bar_view);
        if (topProgressBarView != null) {
            topProgressBarView.setLeftText((int)(isSpeedEnabled ? 0.8 * 100 : 0) + "%");
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
        handler.removeCallbacks(updateUiRunnable);
        getContext().unbindService(this);
        super.onPause();
    }

    final Handler handler = new Handler();

    final Runnable updateUiRunnable = new Runnable() {
        @Override
        public void run() {
            boolean postResultRunnable = false;
            if (measurementService != null) {
                final QualityOfServiceTest qosTest = measurementService.getQosMeasurementClient().getQosTest();
                if (qosTest != null && qosTest.getQosTypeDoneCountMap() != null) {
                    final Map<QosMeasurementType, Float> testTypeProgress = qosTest.getQosTypeTestProgressMap();
                    for (final Map.Entry<QosMeasurementType, Integer> e : qosTest.getQosTypeDoneCountMap().entrySet()) {
                        if (QoSTestEnum.QOS_RUNNING.equals(qosTest.getStatus())) {
                            float progress = (float) e.getValue() / (float) qosTest.getQosTypeTaskCountMap().get(e.getKey());
                            //add progress made during single measurement
                            if (testTypeProgress != null && testTypeProgress.containsKey(e.getKey())) {
                                progress += testTypeProgress.get(e.getKey()) / (float) qosTest.getQosTypeTaskCountMap().get(e.getKey());
                            }
                            qosProgressView.setQosProgress(e.getKey(), progress);
                            if (progress >= 1f) {
                                qosProgressView.finishQosType(e.getKey());
                            }
                            //the total progress (if QoS is enabled) is only from 0.8 to 1.0
                            if (isSpeedEnabled) {
                                topProgressBarView.setLeftText((int)((qosTest.getTotalProgress() * 0.2 + 0.8) * 100) + "%");
                            } else {
                                topProgressBarView.setLeftText((int)(qosTest.getTotalProgress() * 100) + "%");
                            }
                            topProgressBarView.setRightText((int)(qosTest.getTotalProgress() * 100) + "%");
                        }
                        else if (QoSTestEnum.QOS_FINISHED.equals(qosTest.getStatus()) ||
                                QoSTestEnum.ERROR.equals(qosTest.getStatus())) {
                            postResultRunnable = true;
                            qosProgressView.finishQosType(e.getKey());
                            topProgressBarView.setLeftText("100%");
                            topProgressBarView.setRightText("100%");
                        }
                    }
                }
            }

            if (!postResultRunnable) {
                handler.postDelayed(this, 50);
            }
            else {
                handler.postDelayed(showResultsRunnable, 1000);
            }
        }
    };

    final Runnable showResultsRunnable = new Runnable() {
        @Override
        public void run() {
            final QoSResultCollector qoSResultCollector = measurementService.getQosMeasurementClient().getQosResult();
            final SubMeasurementResult subMeasurementResult = SubMeasurementResultParseUtil.parseIntoQosMeasurementResult(qoSResultCollector);
            subMeasurementResult.setStatus(StatusDto.FINISHED);
            measurementService.addSubMeasurementResult(subMeasurementResult);
            final MainActivity activity = (MainActivity) getActivity();
            if (measurementService.hasFollowUpAction()) {
                measurementService.executeFollowUpAction(activity);
            } else {
                measurementService.sendResults(activity, sendingResults);
            }
        }
    };

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "ON SERVICE CONNECTED");
        measurementService = ((MeasurementService.MeasurementServiceBinder) service).getService();
        if (measurementService != null) {
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

    @Override
    public Integer getTitleStringId() {
        return R.string.app_name;
    }

    @Override
    public boolean showHelpButton() {
        return false;
    }

    public boolean isSpeedEnabled() {
        return isSpeedEnabled;
    }

    public void setSpeedEnabled(boolean speedEnabled) {
        isSpeedEnabled = speedEnabled;
    }
}
