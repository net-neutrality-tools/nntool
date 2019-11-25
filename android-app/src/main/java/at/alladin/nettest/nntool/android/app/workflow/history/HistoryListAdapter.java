package at.alladin.nettest.nntool.android.app.workflow.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefSpeedMeasurement;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class HistoryListAdapter extends ArrayAdapter<BriefMeasurementResponse> {

    final DecimalFormat pingFormat = new DecimalFormat("0");

    final DecimalFormat speedFormat = new DecimalFormat("0.#");

    private String timeFormat;

    private class ViewHolder {
        TextView connection;
        TextView date;
        TextView ping;
        TextView up;
        TextView down;
    }

    public HistoryListAdapter(Context context, List<BriefMeasurementResponse> objects) {
        super(context, R.layout.history_list_item, objects);
        if (context != null) {
            timeFormat = context.getString(R.string.date_time_format_pattern);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BriefMeasurementResponse item = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.connection = convertView.findViewById(R.id.history_list_item_connection_text);
            viewHolder.date = convertView.findViewById(R.id.history_list_item_date_text);
            viewHolder.ping = convertView.findViewById(R.id.history_list_item_ping_text);
            viewHolder.down = convertView.findViewById(R.id.history_list_item_down_text);
            viewHolder.up = convertView.findViewById(R.id.history_list_item_up_text);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item.isSpeedMeasurementAvailable()) {
            final BriefSpeedMeasurement m = (BriefSpeedMeasurement) item.getMeasurements().get(MeasurementTypeDto.SPEED);
            viewHolder.ping.setText(m.getRttAverageNs() != null ?
                    pingFormat.format(m.getRttAverageNs() / 1e6) : getContext().getString(R.string.not_available_short));
            viewHolder.down.setText(m.getThroughputAvgDownloadBps() != null ?
                    speedFormat.format(m.getThroughputAvgDownloadBps() / 1e6) : getContext().getString(R.string.not_available_short));
            viewHolder.up.setText(m.getThroughputAvgUploadBps() != null ?
                    speedFormat.format(m.getThroughputAvgUploadBps() / 1e6) : getContext().getString(R.string.not_available_short));
        }
        
        if (item.getNetworkTypeName() != null) {
            viewHolder.connection.setText(item.getNetworkTypeName());
        }

        if (item.getStartTime() != null) {
            viewHolder.date.setText(timeFormat != null ? item.getStartTime().toDateTime().toString(timeFormat) : item.getStartTime().toDateTime().toString());
        }

        return convertView;
    }
}
