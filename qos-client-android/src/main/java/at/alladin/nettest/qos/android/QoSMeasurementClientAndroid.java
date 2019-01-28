package at.alladin.nettest.qos.android;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.alladin.nettest.qos.QoSMeasurementClient;
import at.alladin.nettest.qos.QoSMeasurementClientControlListener;
import at.alladin.nettest.qos.QoSMeasurementContext;
import at.alladin.nettest.qos.android.exception.ClientNotYetRegisteredException;
import at.alladin.nettest.qos.android.exception.NoContextProvidedException;
import at.alladin.nettest.qos.android.impl.TracerouteAndroidImpl;
import at.alladin.nettest.qos.android.impl.TrafficServiceImpl;
import at.alladin.nettest.qos.android.impl.WebsiteTestServiceImpl;
import at.alladin.nettest.qos.android.util.HelperFunctions;
import at.alladin.nettest.qos.android.util.ObtainQoSSettingsTask;
import at.alladin.nettest.shared.model.Client;
import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.helper.Config;
import at.alladin.nntool.client.helper.TestStatus;
import at.alladin.nntool.client.v2.task.QoSTestEnum;
import at.alladin.nntool.client.v2.task.TaskDesc;
import at.alladin.nntool.client.v2.task.service.TestSettings;

import static at.alladin.nntool.client.v2.task.AbstractQoSTask.PARAM_QOS_CONCURRENCY_GROUP;

public class QoSMeasurementClientAndroid extends QoSMeasurementClient implements Runnable {

    private static final Client.ClientType RMBT_CLIENT_TYPE = Client.ClientType.MOBILE;

    private static final String TAG = "QoSMeasurementClientAnd";

    private QoSMeasurementContext measurementContext;

    private Context context;

    private String latestTestUuid;

    public QoSMeasurementClientAndroid(final ClientHolder client, final Context context) {
        this.client = client;
        this.context = context;
    }

    public QoSMeasurementClientAndroid(final QoSMeasurementContext qoSMeasurementContext, final Context context) {
        this.context = context;
        this.measurementContext = qoSMeasurementContext;
        if ("".equals(HelperFunctions.getUuid(context.getApplicationContext()))) {
            new ObtainQoSSettingsTask(measurementContext, context.getApplicationContext()).execute();
        }
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

        if (client == null) { // && "".equals(HelperFunctions.getUuid(context.getApplicationContext()))) { //TODO: we temporarily do not require a user uuid, as the control-service is currently under maintenance (readd && later)
            new ObtainQoSSettingsTask(measurementContext, context.getApplicationContext()).execute();
            Log.i(TAG, "Client not yet registered. Need registration before starting a measurement. " +
                    "\nRegistration process has just started. Call start again after the registration finished.");
            throw new ClientNotYetRegisteredException("Client was not registered before the measurement was started. Registration just started. You don't need to do a thing.");
        }

        threadRunner = new Thread(this);

        threadRunner.start();

    }

    @Override
    public void run() {
        try {

            running.set(true);

            //notify of start
            for (QoSMeasurementClientControlListener listener : this.controlListeners) {
                listener.onMeasurementStarted(enabledTypes);
            }

            //Do basic preparation for the QoS tests
            if (client == null) {
                Log.i(TAG, "Preparing a client object");
                Log.i(TAG, "Using control server @ " + measurementContext.toString());

                final String uuid = HelperFunctions.getUuid(context.getApplicationContext());
                // No measurement request without the server
                final Object request = null;

                // default initialization is not needed for the demo application
                client = ClientHolder.getInstance(measurementContext.getControlServerHost(), "5233", new int[8], new int[5],
                        null, null, null);
            }

            final TestSettings qosTestSettings = new TestSettings();
            qosTestSettings.setCacheFolder(context.getCacheDir());
            qosTestSettings.setWebsiteTestService(new WebsiteTestServiceImpl(context));
            qosTestSettings.setTrafficService(new TrafficServiceImpl());
            qosTestSettings.setTracerouteServiceClazz(TracerouteAndroidImpl.class);
//            if (client != null && client.getControlConnection() != null) {
//                qosTestSettings.setStartTimeNs(client.getControlConnection().getStartTimeNs());
//            }
            qosTestSettings.setUseSsl(Config.QOS_SSL);

            //If the sdk version of the currently operating android device is larger than LOLLIPOP, we provide the DNS servers ourselves, as the DNS library fails for some devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                qosTestSettings.setDnsServerAddressList(HelperFunctions.getDnsServer(context));
            }

            QoSMeasurementClientAndroid.this.testSettings = qosTestSettings;

            // Only execute the desired tasks
            final List<String> toExecute = new ArrayList<>();
            final List<QosMeasurementType> availableTypes = getAvailableTypes();
            for (QosMeasurementType t : enabledTypes) {
                if (t.getValue().endsWith("_browser") || !availableTypes.contains(t)) {
                    continue;
                }
                toExecute.add(t.getValue());
            }

            Log.i(TAG, "Available test types: " + availableTypes.toString());
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

    public void setContext(final Context context) {
        this.context = context;
    }

}
