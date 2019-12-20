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
