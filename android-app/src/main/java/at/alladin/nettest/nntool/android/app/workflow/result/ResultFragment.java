package at.alladin.nettest.nntool.android.app.workflow.result;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestGroupedDetailMeasurementTask;
import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ResultFragment extends Fragment {

    private final static String TAG = ResultFragment.class.getSimpleName();

    private WorkflowResultParameter workflowResultParameter;

    private ProgressBar loadingProgressBar;

    private TextView errorText;

    private ExpandableListView resultListView;

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
                final Context context = getContext();
                if (context != null) {
                    resultListView.setAdapter(new ResultGroupAdapter(getContext(), result.getData().getGroups()));
                    for (int i = 0; i < resultListView.getExpandableListAdapter().getGroupCount(); i++) {
                        resultListView.expandGroup(i);
                    }
                }
            }

        });
        measurementTask.execute();
        return v;
    }

    public WorkflowResultParameter getWorkflowResultParameter() {
        return workflowResultParameter;
    }

    public void setWorkflowResultParameter(WorkflowResultParameter workflowResultParameter) {
        this.workflowResultParameter = workflowResultParameter;
    }
}
