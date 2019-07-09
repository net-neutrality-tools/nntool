package at.alladin.nettest.nntool.android.app.workflow.result;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestGroupedDetailMeasurementTask;
import at.alladin.nettest.nntool.android.app.util.ObjectMapperUtil;
import at.alladin.nettest.nntool.android.app.view.AlladinTextView;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ResultFragment extends Fragment {

    private final static String TAG = ResultFragment.class.getSimpleName();

    private WorkflowResultParameter workflowResultParameter;

    private ProgressBar loadingProgressBar;

    private TextView errorText;

    private ExpandableListView resultListView;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_result, container, false);

        loadingProgressBar = v.findViewById(R.id.result_loading_progress_bar);
        errorText = v.findViewById(R.id.result_loading_error_text);
        resultListView = v.findViewById(R.id.result_list_view);

        final ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        ((TextView) actionBar.getCustomView().findViewById(R.id.action_bar_header)).setText(R.string.title_single_result);
        final AlladinTextView shareIcon = actionBar.getCustomView().findViewById(R.id.action_bar_share_results);
        shareIcon.setVisibility(View.VISIBLE);
        shareIcon.setOnClickListener(view -> {
            shareResult();
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
            }

        });
        measurementTask.execute();

        return v;
    }

    @Override
    public void onDestroyView() {
        final ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.getCustomView().findViewById(R.id.action_bar_share_results).setVisibility(View.GONE);
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
            final String result = ObjectMapperUtil.createBasicObjectMapper().writeValueAsString(measurementResponse);
            final Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, R.string.result_share_text + "\n" + result);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.result_share_subject);
            shareIntent.setType("text/plain");
            getActivity().startActivity(Intent.createChooser(shareIntent, getString(R.string.result_share_intent_title)));

        } catch (Exception ex) {

        }
    }
}
