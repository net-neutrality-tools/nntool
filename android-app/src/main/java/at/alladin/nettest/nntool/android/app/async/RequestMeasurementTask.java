package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final static String TAG = RequestMeasurementTask.class.getSimpleName();

    private final Context context;

    private final OnTaskFinishedCallback<LmapUtil.LmapTaskDescWrapper> callback;

    private BlockingProgressDialog progressDialog;

    public RequestMeasurementTask (final Context context, final OnTaskFinishedCallback<LmapUtil.LmapTaskDescWrapper> callback) {
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
        final LmapUtil.LmapTaskDescWrapper taskDescWrapper = LmapUtil.extractQosTaskDescList(result);
        try {
            Log.d(TAG, new ObjectMapper().writeValueAsString(taskDescWrapper));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (callback != null) {
            callback.onTaskFinished(taskDescWrapper);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
