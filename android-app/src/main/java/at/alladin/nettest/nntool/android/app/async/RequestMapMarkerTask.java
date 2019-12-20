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

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
import at.alladin.nettest.nntool.android.app.util.connection.MapConnection;
import at.alladin.nettest.nntool.android.app.util.connection.ResultConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nntool.shared.map.MapCoordinate;
import at.alladin.nntool.shared.map.MapMarkerRequest;
import at.alladin.nntool.shared.map.MapMarkerResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestMapMarkerTask extends AsyncTask<Void, Void, MapMarkerResponse> {

    private final static String TAG = RequestMapMarkerTask.class.getSimpleName();

    private final Context context;

    private final MapCoordinate position;

    private final OnTaskFinishedCallback<MapMarkerResponse> callback;

    public RequestMapMarkerTask(final MapCoordinate position,
                                final Context context,
                                final OnTaskFinishedCallback<MapMarkerResponse> callback) {
        this.position = position;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected MapMarkerResponse doInBackground(Void... voids) {
        final MapConnection mapConnection = ConnectionUtil.createMapConnection(context);
        Log.d(TAG, "Fetch map marker at coordinate");
        //TODO: mapOptions and filters
        return mapConnection.requestMapMarker(position, new HashMap<>(), new HashMap<>());
    }

    @Override
    protected void onPostExecute(final MapMarkerResponse result) {
        if (callback != null) {
            Log.d(TAG, "Obtained following marker:\n" + (result != null ? result.toString() : "null"));
            callback.onTaskFinished(result);
        }
    }
}
