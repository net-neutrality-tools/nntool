package at.alladin.nettest.nntool.android.app.workflow.history;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.async.DisassociateMeasurementTask;
import at.alladin.nettest.nntool.android.app.async.RequestHistoryTask;
import at.alladin.nettest.nntool.android.app.util.ObjectMapperUtil;
import at.alladin.nettest.nntool.android.app.workflow.ActionBarFragment;
import at.alladin.nettest.nntool.android.app.workflow.result.ResultFragment;
import at.alladin.nettest.nntool.android.app.workflow.result.WorkflowResultParameter;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class HistoryFragment extends ActionBarFragment {

    private final static String TAG = HistoryFragment.class.getSimpleName();

    TextView errorText;
    ProgressBar loadingProgressBar;
    ListView historyListView;

    private int selectedItem = -1;

    public static HistoryFragment newInstance() {
        final HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public Integer getHelpSectionStringId() {
        return R.string.help_link_history_section;
    }

    @Override
    public Integer getTitleStringId() {
        return R.string.title_history;
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

                    //on long click a "disassociate measurement" option is shown
                    //hereby we register that we want to open a context menu on long clicks
                    registerForContextMenu(historyListView);
                    //on normal click, the ResultFragment of the given measurement is shown
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

    public void deleteSelectedItem() {
        final BriefMeasurementResponse selectedMeasurement = (BriefMeasurementResponse) historyListView.getItemAtPosition(selectedItem);
        if (selectedMeasurement != null && selectedMeasurement.getUuid() != null) {

            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "Disassociating measurement w/uuid: " + selectedMeasurement.getUuid());

            final DisassociateMeasurementTask disassociateTask = new DisassociateMeasurementTask(selectedMeasurement.getUuid(), getContext(), result -> {
                if (result != null) {
                    final ArrayAdapter<BriefMeasurementResponse> arrayAdapter = (ArrayAdapter<BriefMeasurementResponse>) historyListView.getAdapter();
                    arrayAdapter.remove(arrayAdapter.getItem(selectedItem));
                    arrayAdapter.notifyDataSetChanged();
                }

                if (loadingProgressBar != null) {
                    loadingProgressBar.setVisibility(View.GONE);
                }
            });

            disassociateTask.execute();

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        final AdapterView.AdapterContextMenuInfo contextMenu = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedItem = contextMenu.position;
        getActivity().getMenuInflater().inflate(R.menu.history_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.history_menu_delete:
                deleteSelectedItem();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
