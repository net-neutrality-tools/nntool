package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ResultConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class DisassociateMeasurementTask extends AsyncTask<Void, Void, ApiResponse<DisassociateResponse>> {

    private final static String TAG = DisassociateMeasurementTask.class.getSimpleName();

    private final Context context;

    private final String measurementUuid;

    private final OnTaskFinishedCallback<ApiResponse<DisassociateResponse>> callback;

    public DisassociateMeasurementTask(final String measurementUuid,
                                       final Context context,
                                       final OnTaskFinishedCallback<ApiResponse<DisassociateResponse>> callback) {
        this.measurementUuid = measurementUuid;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected ApiResponse<DisassociateResponse> doInBackground(Void... voids) {
        final ResultConnection resultConnection = ConnectionUtil.createResultConnection(context);
        Log.d(TAG, "Disassociate measurement for measurement agent uuid: " + PreferencesUtil.getAgentUuid(context) + " and measurement uuid: " + measurementUuid);
        return resultConnection.disassociateMeasurement(PreferencesUtil.getAgentUuid(context), measurementUuid);
    }

    @Override
    protected void onPostExecute(final ApiResponse<DisassociateResponse> result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }
}
