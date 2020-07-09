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

package at.alladin.nettest.qos.android;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.qos.QoSMeasurementClient;
import at.alladin.nettest.qos.QoSMeasurementClientControlListener;
import at.alladin.nettest.qos.android.exception.NoClientProvidedException;
import at.alladin.nettest.qos.android.exception.NoContextProvidedException;
import at.alladin.nettest.qos.android.impl.AudioStreamingServiceAndroidImpl;
import at.alladin.nettest.qos.android.impl.MkitServiceAndroidImpl;
import at.alladin.nettest.qos.android.impl.TracerouteAndroidImpl;
import at.alladin.nettest.qos.android.impl.TrafficServiceImpl;
import at.alladin.nettest.qos.android.impl.WebsiteTestServiceImpl;
import at.alladin.nettest.qos.android.util.HelperFunctions;
import at.alladin.nntool.client.v2.task.AudioStreamingTask;
import at.alladin.nntool.shared.qos.QosMeasurementType;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.helper.TestStatus;
import at.alladin.nntool.client.v2.task.QoSTestEnum;
import at.alladin.nntool.client.v2.task.TaskDesc;
import at.alladin.nntool.client.v2.task.service.TestSettings;
import at.alladin.qos.android.R;
import io.ooni.mk.MKVersion;
import io.ooni.mk.android.MKResources;

import static at.alladin.nntool.client.v2.task.AbstractQoSTask.PARAM_QOS_CONCURRENCY_GROUP;

public class QoSMeasurementClientAndroid extends QoSMeasurementClient implements Runnable {

    //private static final MeasurementAgentTypeDto RMBT_CLIENT_TYPE = MeasurementAgentTypeDto.MOBILE;

    private static final String TAG = "QoSMeasurementClientAnd";

    //as the raw data needs be copied only once, keep track of it
    private static final String PREFERENCE_MKIT_RAW_DATA_COPIED = "qos_mkit_raw_data_copied";

    private Context context;

    private String latestTestUuid;

    public QoSMeasurementClientAndroid(final ClientHolder client, final Context context) {
        this.client = client;
        this.context = context;
    }

    /**
     * You can start the measurement client on a specific context instead of the one provided during construction
     * @param context
     */
    public void start(final Context context) {
        if (context != null) {
            this.context = context;
        }
        start();
    }

    @Override
    public void start() {

        //reset cancelled if it was still set to true from a previous run
        cancelled.set(false);

        if (context == null) {
            //This should be an impossibility by now, so we should be able to remove this check
            throw new NoContextProvidedException();
        }

        threadRunner = new Thread(this);

        threadRunner.start();

    }

    @Override
    public void run() {
        try {

            if (client == null) {
                throw new NoClientProvidedException("Called run without providing the necessary client");
            }

            running.set(true);

            //notify of start
            for (QoSMeasurementClientControlListener listener : this.controlListeners) {
                listener.onMeasurementStarted(enabledTypes);
            }

            //Do basic preparation for the QoS tests
            initiateMkitEnvironment();

            final TestSettings qosTestSettings = new TestSettings();
            qosTestSettings.setCacheFolder(context.getCacheDir());
            qosTestSettings.setWebsiteTestService(new WebsiteTestServiceImpl(context));
            qosTestSettings.setAudioStreamingService(new AudioStreamingServiceAndroidImpl(context));
            qosTestSettings.setTrafficService(new TrafficServiceImpl());
            qosTestSettings.setTracerouteServiceClazz(TracerouteAndroidImpl.class);
            qosTestSettings.setMkitServiceClazz(MkitServiceAndroidImpl.class);
//            if (client != null && client.getControlConnection() != null) {
//                qosTestSettings.setStartTimeNs(client.getControlConnection().getStartTimeNs());
//            }
            //qosTestSettings.setUseSsl(Config.QOS_SSL);

            //If the sdk version of the currently operating android device is larger than LOLLIPOP, we provide the DNS servers ourselves, as the DNS library fails for some devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                qosTestSettings.setDnsServerAddressList(HelperFunctions.getDnsServer(context));
            }

            QoSMeasurementClientAndroid.this.testSettings = qosTestSettings;

            // Only execute the desired tasks
            final List<String> toExecute = new ArrayList<>();
            for (QosMeasurementType t : enabledTypes) {
                toExecute.add(t.getValue());
            }

            Log.i(TAG, "Tests about to be executed " + toExecute.toString());

            //remove QOS tests that are not enabled
            final Iterator<TaskDesc> it = client.getTaskDescList().iterator();
            while (it.hasNext()) {
                final TaskDesc desc = it.next();
                if (!toExecute.contains((String) desc.getParams().get(TaskDesc.QOS_TEST_IDENTIFIER_KEY))) {
                    it.remove();
                    continue;
                } else if (skipConcurrencyGroups != null && skipConcurrencyGroups.size() > 0) {
                    try {
                        final String concurrencyGroup = String.valueOf(desc.getParams().get(PARAM_QOS_CONCURRENCY_GROUP));
                        if (skipConcurrencyGroups.contains(Integer.valueOf(concurrencyGroup))) {
                            it.remove();
                            continue;
                        }
                    } catch (IllegalArgumentException ex) {
                        Log.e(TAG, "Invalid concurrency group given");
                        ex.printStackTrace();
                    }
                }
            }

            //TODO: find a better method to do this
            if (client.getTaskDescList() != null && client.getTaskDescList().size() > 0) {
                qosTestSettings.setUseSsl(client.getTaskDescList().get(0).isEncryption());
            }

            qosTest = new QualityOfServiceTest(client, testSettings, progressListeners);

            client.setStatus(TestStatus.QOS_TEST_RUNNING);

            Log.i(TAG, "Starting actual testing");
            //Call the actual test
            qosResult = qosTest.call();

            Log.i(TAG, "Results are in");

            if (!cancelled.get()) {
                if (qosResult != null && !qosTest.getStatus().equals(QoSTestEnum.ERROR)) {
//                    client.sendQoSResult(qosResult);
                }

                //Store the latest test's uuid, if the test finished successfully
//                latestTestUuid = client.getTestUuid();

                //notify of result
                for (QoSMeasurementClientControlListener listener : controlListeners) {
                    listener.onMeasurementFinished(latestTestUuid, qosResult);
                }

            } else {
                for (QoSMeasurementClientControlListener listener : controlListeners) {
                    listener.onMeasurementStopped();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //notify of error
            for (QoSMeasurementClientControlListener listener : controlListeners) {
                listener.onMeasurementError(ex);
            }
        }

        running.set(false);
    }

    public String getCollectorUrl() {
        return client != null ? client.getCollectorUrl() : null;
    }

    public void setContext(final Context context) {
        this.context = context;
    }

    private void initiateMkitEnvironment() {
        System.loadLibrary("measurement_kit");

        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREFERENCE_MKIT_RAW_DATA_COPIED, false)) {

            Log.i(TAG, "Copying mkit data");
            MKResources.copyCABundle(this.context, R.raw.cacert);
            MKResources.copyGeoIPCountryDB(this.context, R.raw.country);
            MKResources.copyGeoIPASNDB(this.context, R.raw.asn);

            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREFERENCE_MKIT_RAW_DATA_COPIED, true).apply();
        }

        MkitServiceAndroidImpl.caBundlePath = MKResources.getCABundlePath(this.context);
        MkitServiceAndroidImpl.geoIPASNDBPath = MKResources.getGeoIPASNDBPath(this.context);
        MkitServiceAndroidImpl.geoIPCountryDBPath = MKResources.getGeoIPCountryDBPath(this.context);
        Log.i(TAG, "Initiated mkit measurement-environment w/version: " + MKVersion.getVersionMK());
    }

}
