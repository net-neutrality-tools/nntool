package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestAgentIpTask extends AsyncTask<Void, Void, Map<IpResponse.IpVersion, IpResponse>> {

    private final static String TAG = RequestAgentIpTask.class.getSimpleName();

    private final Context context;

    private final OnTaskFinishedCallback<Map<IpResponse.IpVersion, IpResponse>> callback;

    public RequestAgentIpTask(final Context context,
                              final OnTaskFinishedCallback<Map<IpResponse.IpVersion, IpResponse>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Map<IpResponse.IpVersion, IpResponse> doInBackground(Void... voids) {
        final ControllerConnection controllerConnection = ConnectionUtil.createControllerConnection(context);
        final Map<IpResponse.IpVersion, IpResponse> ret = new HashMap<>();
        Log.d(TAG, "Requesting measurement peers");
        for (IpResponse.IpVersion ipVersion : IpResponse.IpVersion.values()) {
            try {
                final IpResponse response = controllerConnection.getIpAddress(ipVersion);
                if (response != null) {
                    ret.put(ipVersion, response);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    protected void onPostExecute(final Map<IpResponse.IpVersion, IpResponse> result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }

}
