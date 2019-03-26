package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.RequestUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class RequestMeasurementTask extends AsyncTask<Void, Void, LmapControlDto> {

    private final Context context;

    private final OnTaskFinishedCallback<LmapControlDto> callback;

    private BlockingProgressDialog progressDialog;

    public RequestMeasurementTask (final Context context, final OnTaskFinishedCallback<LmapControlDto> callback) {
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
        if (callback != null) {
            callback.onTaskFinished(result);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
