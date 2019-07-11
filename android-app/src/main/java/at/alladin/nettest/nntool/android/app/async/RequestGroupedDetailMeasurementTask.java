package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ResultConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestGroupedDetailMeasurementTask extends AsyncTask<Void, Void, ApiResponse<DetailMeasurementResponse>> {

    private final static String TAG = RequestGroupedDetailMeasurementTask.class.getSimpleName();

    private final Context context;

    private final String measurementUuid;

    private final OnTaskFinishedCallback<ApiResponse<DetailMeasurementResponse>> callback;

    public RequestGroupedDetailMeasurementTask(final String measurementUuid,
                                               final Context context,
                                               final OnTaskFinishedCallback<ApiResponse<DetailMeasurementResponse>> callback) {
        this.measurementUuid = measurementUuid;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected ApiResponse<DetailMeasurementResponse> doInBackground(Void... voids) {
        final ResultConnection resultConnection = ConnectionUtil.createResultConnection(context);
        Log.d(TAG, "Fetch grouped detail measurement for measurement agent uuid: " + PreferencesUtil.getAgentUuid(context) + " and measurement uuid: " + measurementUuid);
        return resultConnection.requestGroupedDetails(PreferencesUtil.getAgentUuid(context), measurementUuid);
    }

    @Override
    protected void onPostExecute(final ApiResponse<DetailMeasurementResponse> result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }
}
