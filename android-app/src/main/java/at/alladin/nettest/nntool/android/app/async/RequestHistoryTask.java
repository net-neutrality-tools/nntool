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

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestHistoryTask extends AsyncTask<Void, Void, ApiResponse<ApiPagination<BriefMeasurementResponse>>> {

    private final static String TAG = RequestHistoryTask.class.getSimpleName();

    private final Context context;

    private final OnTaskFinishedCallback<ApiResponse<ApiPagination<BriefMeasurementResponse>>> callback;

    public RequestHistoryTask(final Context context,
                              final OnTaskFinishedCallback<ApiResponse<ApiPagination<BriefMeasurementResponse>>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected ApiResponse<ApiPagination<BriefMeasurementResponse>> doInBackground(Void... voids) {
        final ResultConnection resultConnection = ConnectionUtil.createResultConnection(context);
        Log.d(TAG, "Fetch history for measurement agent uuid: " + PreferencesUtil.getAgentUuid(context));
        return resultConnection.requestHistory(PreferencesUtil.getAgentUuid(context));
    }

    @Override
    protected void onPostExecute(final ApiResponse<ApiPagination<BriefMeasurementResponse>> result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }
}
