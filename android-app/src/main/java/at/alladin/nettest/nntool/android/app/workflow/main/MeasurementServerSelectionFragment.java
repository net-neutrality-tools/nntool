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

import at.alladin.nettest.nntool.android.app.MainActivity;
import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.AbstractFullScreenDialogFragment;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class MeasurementServerSelectionFragment extends AbstractFullScreenDialogFragment {

    private final static String TAG = MeasurementServerSelectionFragment.class.getSimpleName();

    private ListView serverListView;

    private List<SpeedMeasurementPeerResponse.SpeedMeasurementPeer> measurementPeerList;

    private ServerItem selectedServerItem;

    /**
     *
     * @return
     */
    public static MeasurementServerSelectionFragment newInstance(final List<SpeedMeasurementPeerResponse.SpeedMeasurementPeer> measurementPeerList) {
        final MeasurementServerSelectionFragment fragment = new MeasurementServerSelectionFragment();
        fragment.setMeasurementPeerList(measurementPeerList);
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

        Integer defaultIndex = null;
        Integer selectedIndex = null;
        final String selectedIdentifier = ((MainActivity) getContext()).getSelectedMeasurementPeerIdentifier();
        for (int i = 0; i < measurementPeerList.size(); i++) {
            SpeedMeasurementPeerResponse.SpeedMeasurementPeer p = measurementPeerList.get(i);
            serverItemList.add(new ServerItem(p.getName(), p.getDescription(), p.getIdentifier()));
            if (defaultIndex == null && p.isDefaultPeer()) {
                defaultIndex = i;
            }
            if (p.getIdentifier() != null && p.getIdentifier().equals(selectedIdentifier)) {
                selectedIndex = i;
            }
        }
        if (defaultIndex == null) {
            defaultIndex = 0;
        }

        final ServerListAdapter adapter = new ServerListAdapter(getContext(), serverItemList);
        adapter.setSelectedIndex(selectedIndex != null ? selectedIndex : defaultIndex);
        selectedServerItem = adapter.getItem(selectedIndex != null ? selectedIndex : defaultIndex);

        serverListView.setAdapter(adapter);
        serverListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        serverListView.setOnItemClickListener((parent, view, position, id) -> {
            adapter.setSelectedIndex(position);
            selectedServerItem = adapter.getItem(position);
            adapter.notifyDataSetChanged();
        });
    }

    public List<SpeedMeasurementPeerResponse.SpeedMeasurementPeer> getMeasurementPeerList() {
        return measurementPeerList;
    }

    public void setMeasurementPeerList(List<SpeedMeasurementPeerResponse.SpeedMeasurementPeer> measurementPeerList) {
        this.measurementPeerList = measurementPeerList;
    }

    public String getSelectedServerIdentifier() {
        return selectedServerItem.identifier;
    }

    private class ServerItem {
        String name;
        String description;
        String identifier;

        public ServerItem(String name, String description, String identifier) {
            this.name = name;
            this.description = description;
            this.identifier = identifier;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }
    }

    private class ServerListAdapter extends ArrayAdapter<ServerItem> {

        private int selectedIndex = -1;

        private class ViewHolder {
            RadioButton radioButton;
            TextView name;
            TextView description;
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
                vh.description = v.findViewById(R.id.text_server_country);
                vh.radioButton = v.findViewById(R.id.radio_server_selected);
                v.setTag(vh);
            }
            else {
                vh = (ViewHolder) v.getTag();
            }

            final ServerItem item = getItem(position);

            if (item != null) {
                vh.name.setText(item.getName());
                if (item.getDescription() != null) {
                    vh.description.setVisibility(View.VISIBLE);
                    vh.description.setText(item.getDescription());
                }
                else {
                    vh.description.setVisibility(View.GONE);
                }
                vh.radioButton.setChecked(position == selectedIndex);
            }

            return v;
        }
    }
}
