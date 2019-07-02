package at.alladin.nettest.nntool.android.app.workflow.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.RequestHistoryTask;

import static android.support.constraint.Constraints.TAG;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class HistoryFragment extends Fragment {

    private final static String TAG = HistoryFragment.class.getSimpleName();

    public static HistoryFragment newInstance() {
        final HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.history_fragment, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        final RequestHistoryTask historyTask = new RequestHistoryTask(getContext(), r -> {
            try {
                final String json = new ObjectMapper().writeValueAsString(r);
                Log.d(TAG, json);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        });

        historyTask.execute();
    }
}
