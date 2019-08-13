package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ResultConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestFulllMeasurementTask extends AsyncTask<Void, Void, ApiResponse<FullMeasurementResponse>> {

    private final static String TAG = RequestFulllMeasurementTask.class.getSimpleName();

    private final Context context;

    private final String measurementUuid;

    private final OnTaskFinishedCallback<ApiResponse<FullMeasurementResponse>> callback;

    public RequestFulllMeasurementTask(final String measurementUuid,
                                       final Context context,
                                       final OnTaskFinishedCallback<ApiResponse<FullMeasurementResponse>> callback) {
        this.measurementUuid = measurementUuid;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected ApiResponse<FullMeasurementResponse> doInBackground(Void... voids) {
        final ResultConnection resultConnection = ConnectionUtil.createResultConnection(context);
        Log.d(TAG, "Fetch full measurement for measurement agent uuid: " + PreferencesUtil.getAgentUuid(context) + " and measurement uuid: " + measurementUuid);
        return resultConnection.requestFullMeasurement(PreferencesUtil.getAgentUuid(context), measurementUuid);
    }

    @Override
    protected void onPostExecute(final ApiResponse<FullMeasurementResponse> result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }
}
