package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.LmapUtil;
import at.alladin.nettest.nntool.android.app.util.RequestUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nntool.client.v2.task.TaskDesc;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestMeasurementTask extends AsyncTask<Void, Void, LmapControlDto> {

    private final Context context;

    private final OnTaskFinishedCallback<List<TaskDesc>> callback;

    private BlockingProgressDialog progressDialog;

    public RequestMeasurementTask (final Context context, final OnTaskFinishedCallback<List<TaskDesc>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new BlockingProgressDialog.Builder(context)
                .setCancelable(false)
                .setMessage(R.string.task_measurement_initiation_dialog_info)
                .build();
        progressDialog.show();
    }

    @Override
    protected LmapControlDto doInBackground(Void... voids) {
        final ControllerConnection controllerConnection = ConnectionUtil.createControllerConnection(context);
        return controllerConnection.requestMeasurement(
                RequestUtil.prepareMeasurementInitiationRequest(context));
    }

    @Override
    protected void onPostExecute(LmapControlDto result) {
        final List<TaskDesc> taskDescList = LmapUtil.extractQosTaskDescList(result);
        if (callback != null) {
            callback.onTaskFinished(taskDescList);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
