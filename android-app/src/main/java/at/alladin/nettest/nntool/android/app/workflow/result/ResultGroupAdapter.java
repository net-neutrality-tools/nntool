package at.alladin.nettest.nntool.android.app.workflow.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
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
public class ResultGroupAdapter extends BaseExpandableListAdapter {

    private final List<DetailMeasurementGroup> detailMeasurementGroupList;

    private final Context context;

    public ResultGroupAdapter(Context context, List<DetailMeasurementGroup> objects) {
        this.detailMeasurementGroupList = objects;
        this.context = context;
    }

    private class ViewHolder {
        AlladinTextView icon;
        TextView title;
        TextView description;
    }

    private class ItemViewHolder {
        TextView title;
        TextView value;
        TextView unit;
    }

    @Override
    public int getGroupCount() {
        return detailMeasurementGroupList == null ? 0 : detailMeasurementGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return detailMeasurementGroupList == null || detailMeasurementGroupList.size() - 1 < groupPosition
                ? 0 : detailMeasurementGroupList.get(groupPosition).getItems() == null
                ? 0 : detailMeasurementGroupList.get(groupPosition).getItems().size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return detailMeasurementGroupList == null || detailMeasurementGroupList.size() - 1 < groupPosition ? null : detailMeasurementGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        final DetailMeasurementGroup group = detailMeasurementGroupList == null || detailMeasurementGroupList.size() - 1 < groupPosition ? null : detailMeasurementGroupList.get(groupPosition);
        if (group == null) {
            return null;
        }
        return group.getItems() == null || group.getItems().size() - 1 < childPosition ? null : group.getItems().get(childPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final DetailMeasurementGroup item = (DetailMeasurementGroup) getGroup(groupPosition);

        ResultGroupAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.result_list_group, parent, false);
            viewHolder = new ResultGroupAdapter.ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.result_list_group_icon);
            viewHolder.title = convertView.findViewById(R.id.result_list_group_title);
            viewHolder.description = convertView.findViewById(R.id.result_list_group_description);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ResultGroupAdapter.ViewHolder) convertView.getTag();
        }

        if (item.getIconCharacter() != null) {
            viewHolder.icon.setText(item.getIconCharacter());
        } else {
            viewHolder.icon.setText("");
        }

        if (item.getTitle() != null) {
            viewHolder.title.setText(item.getTitle());
        } else {
            viewHolder.title.setText("");
        }

        if (item.getDescription() != null) {
            viewHolder.description.setText(item.getDescription());
        } else {
            viewHolder.description.setText("");
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final DetailMeasurementGroupItem item = (DetailMeasurementGroupItem) getChild(groupPosition, childPosition);

        ResultGroupAdapter.ItemViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.result_list_group_item, parent, false);
            viewHolder = new ResultGroupAdapter.ItemViewHolder();
            viewHolder.title = convertView.findViewById(R.id.result_list_group_item_title);
            viewHolder.value = convertView.findViewById(R.id.result_list_group_item_value);
            viewHolder.unit = convertView.findViewById(R.id.result_list_group_item_unit);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ResultGroupAdapter.ItemViewHolder) convertView.getTag();
        }

        if (item.getTitle() != null) {
            viewHolder.title.setText(item.getTitle());
        } else {
            viewHolder.title.setText("");
        }

        if (item.getValue() != null) {
            viewHolder.value.setText(item.getValue());
        } else {
            viewHolder.value.setText("");
        }

        if (item.getUnit() != null) {
            viewHolder.unit.setText(item.getUnit());
        } else {
            viewHolder.unit.setText("");
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
