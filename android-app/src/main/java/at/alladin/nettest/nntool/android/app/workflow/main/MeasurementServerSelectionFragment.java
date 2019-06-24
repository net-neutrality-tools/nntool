package at.alladin.nettest.nntool.android.app.workflow.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.AbstractFullScreenDialogFragment;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class MeasurementServerSelectionFragment extends AbstractFullScreenDialogFragment {

    private final static String TAG = MeasurementServerSelectionFragment.class.getSimpleName();

    private ListView serverListView;

    /**
     *
     * @return
     */
    public static MeasurementServerSelectionFragment newInstance() {
        final MeasurementServerSelectionFragment fragment = new MeasurementServerSelectionFragment();
        return fragment;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_measurement_server_selection;
    }

    @Override
    public void onViewCreated(View v) {
        setToolbarTitle("Measurement Server", true);

        serverListView = v.findViewById(R.id.listview_measurement_server_list);
        final List<ServerItem> serverItemList = new ArrayList<>();

        //TODO: fill server list
        serverItemList.add(new ServerItem(getString(R.string.measurement_server_selection_default_server), null));

        final ServerListAdapter adapter = new ServerListAdapter(getContext(), serverItemList);
        //TODO: select current server
        adapter.setSelectedIndex(0);

        serverListView.setAdapter(adapter);
        serverListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        serverListView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedIndex(position);
            adapter.notifyDataSetChanged();
        });
    }

    private class ServerItem {
        String name;
        String country;

        public ServerItem(String name, String country) {
            this.name = name;
            this.country = country;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    private class ServerListAdapter extends ArrayAdapter<ServerItem> {

        private int selectedIndex = -1;

        private class ViewHolder {
            RadioButton radioButton;
            TextView name;
            TextView country;
        }

        public ServerListAdapter(final Context context, final List<ServerItem> items) {
            super(context, R.layout.measurement_server_selection_item, items);
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public void setSelectedIndex(int selectedIndex) {
            this.selectedIndex = selectedIndex;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder vh = null;

            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.measurement_server_selection_item, parent, false);
                vh = new ViewHolder();
                vh.name = v.findViewById(R.id.text_server_name);
                vh.country = v.findViewById(R.id.text_server_country);
                vh.radioButton = v.findViewById(R.id.radio_server_selected);
                v.setTag(vh);
            }
            else {
                vh = (ViewHolder) v.getTag();
            }

            final ServerItem item = getItem(position);

            if (item != null) {
                vh.name.setText(item.getName());
                if (item.getCountry() != null) {
                    vh.country.setVisibility(View.VISIBLE);
                    vh.country.setText(item.getCountry());
                }
                else {
                    vh.country.setVisibility(View.GONE);
                }
                vh.radioButton.setChecked(position == selectedIndex);
            }

            return v;
        }
    }
}
