/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
        final ControllerConnection controllerConnection = ConnectionUtil.createControllerConnection(context);
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
