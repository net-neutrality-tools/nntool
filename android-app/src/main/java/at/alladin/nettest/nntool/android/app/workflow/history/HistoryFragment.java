package at.alladin.nettest.nntool.android.app.workflow.history;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestHistoryTask;
import at.alladin.nettest.nntool.android.app.util.ObjectMapperUtil;
import at.alladin.nettest.nntool.android.app.workflow.result.ResultFragment;
import at.alladin.nettest.nntool.android.app.workflow.result.WorkflowResultParameter;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class HistoryFragment extends Fragment {

    private final static String TAG = HistoryFragment.class.getSimpleName();

    TextView errorText;
    ProgressBar loadingProgressBar;
    ListView historyListView;

    public static HistoryFragment newInstance() {
        final HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_history, container, false);

        loadingProgressBar = v.findViewById(R.id.history_loading_progress_bar);
        errorText = v.findViewById(R.id.history_loading_error_text);
        historyListView = v.findViewById(R.id.history_list_view);

        final RequestHistoryTask historyTask = new RequestHistoryTask(getContext(), r -> {
            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.GONE);
            }

            if (r == null
                    || r.getData() == null
                    || r.getData().getContent() == null
                    || r.getData().getContent().size() == 0) {
                if (errorText != null) {
                    errorText.setVisibility(View.VISIBLE);
                }
            }
            else {
                final Context context = getContext();
                if (context != null) {
                    historyListView.setAdapter(new HistoryListAdapter(getContext(), r.getData().getContent()));
                    historyListView.setOnItemClickListener((parent, view, position, id) -> {
                        final BriefMeasurementResponse response = (BriefMeasurementResponse) historyListView.getItemAtPosition(position);
                        if (response != null) {
                            final WorkflowResultParameter resultParameter = new WorkflowResultParameter();
                            resultParameter.setMeasurementUuid(response.getUuid());
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.main_fragment_layout, ResultFragment.newInstance(resultParameter))
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                }
            }

            //TODO remove: (testing purpose)
            try {
                final String json = ObjectMapperUtil.createBasicObjectMapper().writeValueAsString(r);
                Log.d(TAG, json);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        });

        historyTask.execute();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
