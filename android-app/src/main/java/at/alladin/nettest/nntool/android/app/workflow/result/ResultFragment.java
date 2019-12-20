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

package at.alladin.nettest.nntool.android.app.workflow.result;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestGroupedDetailMeasurementTask;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;
import at.alladin.nettest.nntool.android.app.workflow.result.qos.ResultQoSFragment;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ResultFragment extends ActionBarFragment {

    private final static String TAG = ResultFragment.class.getSimpleName();

    private WorkflowResultParameter workflowResultParameter;

    private ProgressBar loadingProgressBar;

    private TextView errorText;

    private ExpandableListView resultListView;

    private View toQoSButton;

    private DetailMeasurementResponse measurementResponse;

    public static ResultFragment newInstance() {
        final ResultFragment fragment = new ResultFragment();
        return fragment;
    }

    public static ResultFragment newInstance (WorkflowParameter workflowParameter) {
        final ResultFragment fragment = new ResultFragment();
        if (workflowParameter instanceof WorkflowResultParameter) {
            fragment.setWorkflowResultParameter((WorkflowResultParameter) workflowParameter);
        }
        return fragment;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.action_bar_share_results_action);
        menuItem.setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_bar_share_results_action:
                shareResult();
                return true;
        }
        //the help option is handled by the superclass
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_result, container, false);

        loadingProgressBar = v.findViewById(R.id.result_loading_progress_bar);
        errorText = v.findViewById(R.id.result_loading_error_text);
        resultListView = v.findViewById(R.id.result_list_view);
        toQoSButton = v.findViewById(R.id.result_to_qos_button);

        toQoSButton.setOnClickListener( view -> {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_layout, ResultQoSFragment.newInstance(workflowResultParameter))
                    .addToBackStack(null)
                    .commit();

        });

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final RequestGroupedDetailMeasurementTask measurementTask = new RequestGroupedDetailMeasurementTask(workflowResultParameter.getMeasurementUuid(), getContext(), result -> {
            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.GONE);
            }

            if (result == null
                    || result.getData() == null
                    || result.getData().getGroups() == null
                    || result.getData().getGroups().size() == 0) {
                if (errorText != null) {
                    errorText.setVisibility(View.VISIBLE);
                }
            }
            else {
                measurementResponse = result.getData();
                final Context context = getContext();
                if (context != null) {
                    final int width = displayMetrics.widthPixels;
                    resultListView.setIndicatorBoundsRelative(
                            width - pixelOffsetToDps(60, displayMetrics.density),
                            width - pixelOffsetToDps(20, displayMetrics.density)
                    );
                    resultListView.setAdapter(new ResultGroupAdapter(getContext(), measurementResponse.getGroups()));
                    for (int i = 0; i < resultListView.getExpandableListAdapter().getGroupCount(); i++) {
                        resultListView.expandGroup(i);
                    }
                }
                if (measurementResponse.getHasQoSResults() == null || !measurementResponse.getHasQoSResults()) {
                    toQoSButton.setVisibility(View.GONE);
                }
            }

        });
        measurementTask.execute();

        return v;
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.title_single_result;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_result_section;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private int pixelOffsetToDps(int offsetPixels, float density) {
        return (int) (offsetPixels * density + 0.5f);
    }

    public WorkflowResultParameter getWorkflowResultParameter() {
        return workflowResultParameter;
    }

    public void setWorkflowResultParameter(WorkflowResultParameter workflowResultParameter) {
        this.workflowResultParameter = workflowResultParameter;
    }

    public void shareResult() {
        if (measurementResponse == null) {
            return;
        }
        Log.d(TAG, "Sharing measurement result");
        try {
            final Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, measurementResponse.getShareMeasurementText());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.result_share_subject);
            shareIntent.setType("text/plain");
            getActivity().startActivity(Intent.createChooser(shareIntent, getString(R.string.result_share_intent_title)));

        } catch (Exception ex) {

        }
    }
}
