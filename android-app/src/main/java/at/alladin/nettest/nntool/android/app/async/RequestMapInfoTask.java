package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.connection.MapConnection;
import at.alladin.nntool.shared.map.info.MapInfoResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestMapInfoTask extends AsyncTask<Void, Void, MapInfoResponse> {

    private final static String TAG = RequestMapInfoTask.class.getSimpleName();

    private final Context context;

    private final OnTaskFinishedCallback<MapInfoResponse> callback;

    public RequestMapInfoTask(final Context context,
                              final OnTaskFinishedCallback<MapInfoResponse> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected MapInfoResponse doInBackground(Void... voids) {
        final MapConnection mapConnection = ConnectionUtil.createMapConnection(context);
        return mapConnection.requestMapInfo();
    }

    @Override
    protected void onPostExecute(final MapInfoResponse result) {
        if (callback != null) {
            Log.d(TAG, "Obtained map info:\n" + (result != null ? result.toString() : "null"));
            callback.onTaskFinished(result);
        }
    }
}
