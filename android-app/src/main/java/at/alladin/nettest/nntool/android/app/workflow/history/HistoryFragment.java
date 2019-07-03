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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestHistoryTask;

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
                }
            }

            //TODO remove: (testing purpose)
            try {
                final String json = new ObjectMapper().writeValueAsString(r);
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
