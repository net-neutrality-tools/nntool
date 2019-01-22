/*******************************************************************************
 * Copyright 2013-2017 alladin-IT GmbH
 * Copyright 2014-2016 SPECURE GmbH
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

package at.alladin.nettest.qos.android.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.qos.QoSMeasurementContext;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nettest.shared.model.response.SettingsResponse;
//import at.alladin.rmbt.client.helper.ControlServerConnection;

/**
 * 
 * @author
 * 
 */
public class ObtainQoSSettingsTask extends AsyncTask<Void, Void, SettingsResponse>
{

    /**
	 *
	 */
    private static final String DEBUG_TAG = "ObtainQoSSettingsTask";

    /**
	 *
	 */
//    private ControlServerConnection serverConn;

    /**
	 *
	 */
    private final WeakReference<Context> context;

    private final QoSMeasurementContext qosMeasurementContext;

    /**
	 *
	 */
    public ObtainQoSSettingsTask(final QoSMeasurementContext qoSMeasurementContext, final Context context)
    {
        this.context = new WeakReference<>(context);
        this.qosMeasurementContext = qoSMeasurementContext;
    }
    
    /**
	 *
	 */
    @Override
    protected SettingsResponse doInBackground(final Void... params)
    {
        final SettingsResponse ret = new SettingsResponse();

        // Temporary hard-coded settings response (I18N is handled by the server, therefore the server-less version contains only a single lang)
        final List<SettingsResponse.QosMeasurementTypeResponse> responseList = new ArrayList<>();

        SettingsResponse.QosMeasurementTypeResponse qosResponse = new SettingsResponse.QosMeasurementTypeResponse();
        qosResponse.setName("TCP Ports");
        qosResponse.setDescription("TCP is the most common connection-oriented internet protocol. Typical services http for websites or smtp for e-mail.");
        qosResponse.setType(QosMeasurementType.TCP);
        responseList.add(qosResponse);

        qosResponse = new SettingsResponse.QosMeasurementTypeResponse();
        qosResponse.setName("UDP Ports");
        qosResponse.setDescription("UDP is an important connectionless internet protocol. Typical services real-time communication like VoIP or video streaming.");
        qosResponse.setType(QosMeasurementType.UDP);
        responseList.add(qosResponse);
        ret.setQosMeasurementTypes(responseList);

        return ret;

        //TODO: temporarily removed the actual obtaining of the settings (re-enbale sth. similar once the control service is up and running)
//        if (context.get() == null) {
//            return null;
//        }
////        serverConn = new ControlServerConnection(context.getApplicationContext());
//        serverConn = new ControlServerConnection(qosMeasurementContext.isUseTls(), qosMeasurementContext.getControlServerHost(),
//                null, qosMeasurementContext.getControlServerPort());
//        SettingsRequest settingsRequest = HelperFunctions.fillBasicInfo(SettingsRequest.class, context.get());
//        if (settingsRequest == null) {
//            //TODO: make custom exception
//            throw new NoContextProvidedException();
//        }
//        settingsRequest.setClient(new Client());
//        settingsRequest.getClient().setUuid(HelperFunctions.getUuid(context.get().getApplicationContext()));
//        settingsRequest.getClient().setTermsAndConditionsAccepted(true);
//        settingsRequest.getClient().setTermsAndConditionsAcceptedVersion(2);
//        return serverConn.requestSettings(settingsRequest);
    }

    /**
	 * 
	 */
    @Override
    protected void onCancelled()
    {
//        if (serverConn != null)
//        {
//            serverConn = null;
//        }
    }

    /**
	 * 
	 */
    @Override
    protected void onPostExecute(final SettingsResponse settings)
    {
            if (settings != null && context.get() != null) {

                Log.i(DEBUG_TAG, "Settings obtained");

                /* UUID */
                final String uuid = settings.getClient().getUuid();
                if (uuid != null && uuid.length() != 0) {
                    HelperFunctions.setUuid(uuid, context.get().getApplicationContext());
                }

            }
            else {
                Log.i(DEBUG_TAG, "Empty settings response");
            }
    }
    
}
