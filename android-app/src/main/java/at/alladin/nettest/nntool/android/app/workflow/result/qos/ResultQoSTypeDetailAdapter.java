package at.alladin.nettest.nntool.android.app.workflow.result.qos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.view.AlladinTextView;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.EvaluatedQoSResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class ResultQoSTypeDetailAdapter extends ArrayAdapter<EvaluatedQoSResult> {

    private final Context context;

    public ResultQoSTypeDetailAdapter(final Context context, final List<EvaluatedQoSResult> qosResults) {
        super(context, R.layout.qos_result_type_detail_list_item, qosResults);
        this.context = context;
    }

    private class ViewHolder {
        TextView testNumber;
        TextView summary;
        AlladinTextView checkmark;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final EvaluatedQoSResult item = getItem(position);

        ResultQoSTypeDetailAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.qos_result_type_detail_list_item, parent, false);
            viewHolder = new ResultQoSTypeDetailAdapter.ViewHolder();
            viewHolder.testNumber = convertView.findViewById(R.id.result_test_header);
            viewHolder.summary = convertView.findViewById(R.id.result_summary);
            viewHolder.checkmark = convertView.findViewById(R.id.result_checkmark);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ResultQoSTypeDetailAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.testNumber.setText(context.getResources().getString(R.string.result_qos_test_list_item_header, position + 1));
        viewHolder.summary.setText(item.getSummary());
        if (item.getEvaluationCount() == item.getSuccessCount()) {
            viewHolder.checkmark.setText(R.string.ifont_check);
            viewHolder.checkmark.setTextColor(context.getResources().getColor(R.color.result_qos_checkmark_good));
        } else {
            viewHolder.checkmark.setText(R.string.ifont_close);
            viewHolder.checkmark.setTextColor(context.getResources().getColor(R.color.result_qos_checkmark_bad));
        }

        return convertView;
    }


}
