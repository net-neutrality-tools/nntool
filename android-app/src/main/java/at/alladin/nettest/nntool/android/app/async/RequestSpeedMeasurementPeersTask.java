package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.RequestUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.nntool.android.app.util.connection.ResultConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestSpeedMeasurementPeersTask extends AsyncTask<Void, Void, SpeedMeasurementPeerResponse> {

    private final static String TAG = RequestSpeedMeasurementPeersTask.class.getSimpleName();

    private final Context context;

    private final OnTaskFinishedCallback<SpeedMeasurementPeerResponse> callback;

    public RequestSpeedMeasurementPeersTask(final Context context,
                                            final OnTaskFinishedCallback<SpeedMeasurementPeerResponse> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected SpeedMeasurementPeerResponse doInBackground(Void... voids) {
        final ControllerConnection controllerConnection = ConnectionUtil.createControllerConnection(context, false);
        Log.d(TAG, "Requesting measurement peers");
        return controllerConnection.getMeasurementPeers(RequestUtil.prepareApiSpeedMeasurementPeerRequest(context));
    }

    @Override
    protected void onPostExecute(final SpeedMeasurementPeerResponse result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }
}
