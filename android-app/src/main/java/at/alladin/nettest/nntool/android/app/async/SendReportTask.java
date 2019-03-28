package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.connection.CollectorConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class SendReportTask extends AsyncTask<Void, Void, MeasurementResultResponse> {

    private final Context context;
    private final OnTaskFinishedCallback<MeasurementResultResponse> callback;
    private BlockingProgressDialog progressDialog;
    private LmapReportDto reportDto;
    private String collectorUrl;

    public SendReportTask(final Context context, final LmapReportDto reportDto,
                          final String collectorUrl, final OnTaskFinishedCallback<MeasurementResultResponse> callback) {
        this.context = context;
        this.callback = callback;
        this.collectorUrl = collectorUrl;
        this.reportDto = reportDto;
        try {
            System.out.println(new ObjectMapper().writeValueAsString(reportDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        progressDialog = new BlockingProgressDialog.Builder(context)
                .setCancelable(false)
                .setMessage(R.string.task_send_report_dialog_info)
                .build();
        progressDialog.show();
    }

    @Override
    protected MeasurementResultResponse doInBackground(Void... voids) {
        final CollectorConnection collectorConnection = ConnectionUtil.createCollectorConnection(collectorUrl);
        return collectorConnection.sendMeasurementReport(reportDto);
    }

    @Override
    protected void onPostExecute(MeasurementResultResponse result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
