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
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.view.BottomMeasurementResultSummaryView;
import at.alladin.nettest.nntool.android.app.view.MeasurementRecentResultSelectorView;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;
import at.alladin.nettest.nntool.android.app.workflow.main.TitleFragment;
import com.zafaco.speed.SpeedMeasurementState;

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
        return newInstance(null);
    }

    public static TitleWithRecentResultFragment newInstance(final WorkflowParameter parameter) {
        final TitleWithRecentResultFragment fragment = new TitleWithRecentResultFragment();
        if (parameter instanceof WorkflowRecentResultParameter) {
             final SpeedMeasurementState state = new SpeedMeasurementState();
             state.setMeasurementPhase(SpeedMeasurementState.MeasurementPhase.END);
             state.setProgress(1.0f);
             state.setMeasurementUuid(((WorkflowRecentResultParameter) parameter).getRecentResultUuid());
             fragment.setRecentSpeedMeasurementState(state);
        }
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
            bottomMeasurementResultSummaryView.setDownloadText(String.format(Locale.getDefault(), "%.2f", recentSpeedMeasurementState.getDownloadMeasurement().getThroughputAvgBps() / 1e6));
        } else {
            bottomMeasurementResultSummaryView.setDownloadText(getString(R.string.bottom_measurement_result_summary_view_download_not_available));
        }

        if (recentSpeedMeasurementState.getUploadMeasurement() != null) {
            bottomMeasurementResultSummaryView.setUploadText(String.format(Locale.getDefault(), "%.2f", recentSpeedMeasurementState.getUploadMeasurement().getThroughputAvgBps() / 1e6));
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
        if (measurementService.getJniSpeedMeasurementClient() != null) {
            recentSpeedMeasurementState = measurementService.getJniSpeedMeasurementClient().getSpeedMeasurementState();
        }
        //the recent speed measurement state will be instantiated by the newInstance if no speedmeasurement was executed
        if (recentSpeedMeasurementState != null && bottomMeasurementResultSummaryView != null) {
            setMeasurementStateInBottomView();
            bottomMeasurementResultSummaryView.invalidate();
        }
        //control of the "View your measurement" button is independent of the result summary
        if (recentSpeedMeasurementState != null && recentSpeedMeasurementState.getMeasurementUuid() != null) {
            measurementRecentResultSelectorView.setMeasurementUuid(recentSpeedMeasurementState.getMeasurementUuid());
            measurementRecentResultSelectorView.setVisibility(View.VISIBLE);
        } else {
            measurementRecentResultSelectorView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "ON SERVICE DISCONNECTED");
        measurementService = null;
    }

    public SpeedMeasurementState getRecentSpeedMeasurementState() {
        return recentSpeedMeasurementState;
    }

    public void setRecentSpeedMeasurementState(SpeedMeasurementState recentSpeedMeasurementState) {
        this.recentSpeedMeasurementState = recentSpeedMeasurementState;
    }

}