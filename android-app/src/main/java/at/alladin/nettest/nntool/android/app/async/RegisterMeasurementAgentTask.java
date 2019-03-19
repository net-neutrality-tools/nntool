package at.alladin.nettest.nntool.android.app.async;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.dialog.BlockingProgressDialog;
import at.alladin.nettest.nntool.android.app.util.ControllerConnection;
import at.alladin.nettest.nntool.android.app.util.ControllerService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class RegisterMeasurementAgentTask extends AsyncTask<Void, Void, RegistrationResponse> {

    final OnTaskFinishedCallback<RegistrationResponse> callback;

    final Context context;

    BlockingProgressDialog progressDialog;

    public RegisterMeasurementAgentTask(final Context context, final OnTaskFinishedCallback<RegistrationResponse> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
       progressDialog = new BlockingProgressDialog.Builder(context)
               .setCancelable(false)
               .setMessage(R.string.task_register_agent_dialog_info)
               .build();
       progressDialog.show();
    }

    @Override
    protected RegistrationResponse doInBackground(Void... voids) {
        final ControllerConnection cc = new ControllerConnection(false, "10.9.8.36", null, 8080, "/");
        final RegistrationResponse response = cc.registerMeasurementAgent();
        try {
            Thread.sleep(2000);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(RegistrationResponse result) {
        if (result != null) {

        }

        if (callback != null) {
            callback.onTaskFinished(result);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
