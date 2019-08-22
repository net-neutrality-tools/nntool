package at.alladin.nettest.qos.android;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.alladin.nettest.qos.QoSMeasurementClient;
import at.alladin.nettest.qos.QoSMeasurementClientControlListener;
import at.alladin.nettest.qos.android.exception.NoClientProvidedException;
import at.alladin.nettest.qos.android.exception.NoContextProvidedException;
import at.alladin.nettest.qos.android.impl.TracerouteAndroidImpl;
import at.alladin.nettest.qos.android.impl.TrafficServiceImpl;
import at.alladin.nettest.qos.android.impl.WebsiteTestServiceImpl;
import at.alladin.nettest.qos.android.util.HelperFunctions;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.helper.TestStatus;
import at.alladin.nntool.client.v2.task.QoSTestEnum;
import at.alladin.nntool.client.v2.task.TaskDesc;
import at.alladin.nntool.client.v2.task.service.TestSettings;

import static at.alladin.nntool.client.v2.task.AbstractQoSTask.PARAM_QOS_CONCURRENCY_GROUP;

public class QoSMeasurementClientAndroid extends QoSMeasurementClient implements Runnable {

    //private static final MeasurementAgentTypeDto RMBT_CLIENT_TYPE = MeasurementAgentTypeDto.MOBILE;

    private static final String TAG = "QoSMeasurementClientAnd";

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

            final TestSettings qosTestSettings = new TestSettings();
            qosTestSettings.setCacheFolder(context.getCacheDir());
            qosTestSettings.setWebsiteTestService(new WebsiteTestServiceImpl(context));
            qosTestSettings.setTrafficService(new TrafficServiceImpl());
            qosTestSettings.setTracerouteServiceClazz(TracerouteAndroidImpl.class);
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
                } else if (skipConcurrencyGroups != null && skipConcurrencyGroups.size() > 0) {
                    try {
                        final String concurrencyGroup = String.valueOf(desc.getParams().get(PARAM_QOS_CONCURRENCY_GROUP));
                        if (skipConcurrencyGroups.contains(Integer.valueOf(concurrencyGroup))) {
                            it.remove();
                        }
                    } catch (IllegalArgumentException ex) {
                        Log.e(TAG, "Invalid concurrency group given");
                        ex.printStackTrace();
                    }
                }
            }

            //TODO: probably any other method is better than this:
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

}
