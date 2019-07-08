package at.alladin.nettest.nntool.android.app.workflow.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.view.AlladinTextView;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroup;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroupItem;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultGroupAdapter extends ArrayAdapter<DetailMeasurementGroup> {

    public ResultGroupAdapter(Context context, List<DetailMeasurementGroup> objects) {
        super(context, R.layout.result_list_group, objects);
    }

    private class ViewHolder {
        AlladinTextView icon;
        TextView title;
        TextView description;
        LinearLayout entries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DetailMeasurementGroup item = getItem(position);

        ResultGroupAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.result_list_group, parent, false);
            viewHolder = new ResultGroupAdapter.ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.result_list_group_icon);
            viewHolder.title = convertView.findViewById(R.id.result_list_group_title);
            viewHolder.description = convertView.findViewById(R.id.result_list_group_description);
            viewHolder.entries = convertView.findViewById(R.id.result_list_group_item_entries);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ResultGroupAdapter.ViewHolder) convertView.getTag();
        }

        if (item.getIconCharacter() != null) {
            viewHolder.icon.setText(item.getIconCharacter());
        }

        if (item.getTitle() != null) {
            viewHolder.title.setText(item.getTitle());
        }

        if (item.getDescription() != null) {
            viewHolder.description.setText(item.getDescription());
        }

        if (item.getItems() != null && item.getItems().size() > 0 && viewHolder.entries.getChildCount() == 0) {
            for (DetailMeasurementGroupItem e : item.getItems()) {
                final RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.result_list_group_item, parent, false);

                if (e.getTitle() != null) {
                    ((TextView) relativeLayout.findViewById(R.id.result_list_group_item_title)).setText(e.getTitle());
                }

                if (e.getValue() != null) {
                    ((TextView) relativeLayout.findViewById(R.id.result_list_group_item_value)).setText(e.getValue());
                }

                if (e.getUnit() != null) {
                    ((TextView) relativeLayout.findViewById(R.id.result_list_group_item_unit)).setText(e.getUnit());
                }

                viewHolder.entries.addView(relativeLayout);
            }
        }

        return convertView;
    }
}
