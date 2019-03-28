package at.alladin.nettest.qos;

import static at.alladin.nntool.client.v2.task.AbstractQoSTask.PARAM_QOS_CONCURRENCY_GROUP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.helper.TestStatus;
import at.alladin.nntool.client.v2.task.QoSTestEnum;
import at.alladin.nntool.client.v2.task.TaskDesc;
import at.alladin.nntool.client.v2.task.result.QoSResultCollector;
import at.alladin.nntool.client.v2.task.service.TestSettings;

public class QoSMeasurementClient {

    protected final List<QoSMeasurementClientControlListener> controlListeners = Collections.synchronizedList(new ArrayList<QoSMeasurementClientControlListener>());

    protected final List<QoSMeasurementClientProgressListener> progressListeners = Collections.synchronizedList(new ArrayList<QoSMeasurementClientProgressListener>());

    protected ClientHolder client;

    protected TestSettings testSettings;

    protected AtomicBoolean running;

    protected AtomicBoolean cancelled;

    protected List<QosMeasurementType> enabledTypes;

    protected QualityOfServiceTest qosTest;

    protected QoSResultCollector qosResult;

    protected Thread threadRunner;

    protected List<Integer> skipConcurrencyGroups;

    public QoSMeasurementClient() {
        running = new AtomicBoolean(false);
        cancelled = new AtomicBoolean(false);
        enabledTypes = new ArrayList<>(Arrays.asList(QosMeasurementType.values()));
    }

    /**
     * Start execution of the QosMeasurement Client
     * Will execute all tests that were previously set via setEnabledTypes
     */
    public void start() {
        running.set(true);

        if (client == null || testSettings == null) {
            //TODO: provide default client
            throw new NullPointerException("No client or testSettings set for running the QoS tests");
        }

        final List<String> toExecute = new ArrayList<>();
        for (QosMeasurementType t : enabledTypes) {
            toExecute.add(t.getValue());
        }

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
                    System.out.println("Invalid concurrency group given");
                    ex.printStackTrace();
                }
            }
        }

        qosTest = new QualityOfServiceTest(client, testSettings);
        for (QoSMeasurementClientProgressListener l : progressListeners) {
            qosTest.addQoSProgressListener(l);
        }

        client.setStatus(TestStatus.QOS_TEST_RUNNING);

        threadRunner = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    qosResult = qosTest.call();
//                    qosResult.setTestToken(qosTest.getRMBTClient().getTestParameter().getToken()); //TODO: comment in

                    if (!cancelled.get()) {
                        if (qosResult != null && !qosTest.getStatus().equals(QoSTestEnum.ERROR)) {
//                            client.sendQoSResult(qosResult);  //TODO: comment in
                        }
                        //notify of result
                        for (QoSMeasurementClientControlListener listener : controlListeners) {
//                            listener.onMeasurementFinished(qosTest.getRMBTClient().getTestUuid(), qosResult); //TODO: comment in
                            listener.onMeasurementFinished("", qosResult);
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

                //reset qosTest for GC (we don't need the object anymore)
                qosTest = null;

            }
        });

        threadRunner.start();

        //notify of start
        for (QoSMeasurementClientControlListener listener : this.controlListeners) {
            listener.onMeasurementStarted(enabledTypes);
        }

    }

    /**
     * Interrupt current execution of the QosMeasurementClient
     */
    public void stop() {
        running.set(false);
        cancelled.set(true);
        if (qosTest != null) {
            qosTest.interrupt();
            //reset qosTest for GC
            qosTest = null;
        }
        
        if (threadRunner != null && threadRunner.isAlive() && !threadRunner.isInterrupted()) {
            threadRunner.interrupt();
        }
    }

    /**
     *  Allows to provide a number of concurrencyGroups, where all tests belonging to that group will NOT be part of the QoS test
     * @param concurrencyGroups if concurrencyGroups is null, ALL concurrencyGroups will be executed
     */
    public void skipConcurrencyGroups(List<Integer> concurrencyGroups) {
        this.skipConcurrencyGroups = new ArrayList<>(concurrencyGroups);
    }

    /**
     * Sets the types of QoS tests that shall be executed when start() is called
     * If no call to setEnabledTypes was made, ALL tests are considered enabled
     * @param enabledTypes
     */
    public void setEnabledTypes(List<QosMeasurementType> enabledTypes) {
        if (enabledTypes != null) {
            this.enabledTypes = new ArrayList<>(enabledTypes);
        }
    }

    /**
     * Convenience function to enable a single QosMeasurementType w/out the need to put it into a list
     * @param enabledType
     */
    public void setEnabledType(QosMeasurementType enabledType) {
        if (enabledType != null) {
            this.enabledTypes = new ArrayList<>();
            this.enabledTypes.add(enabledType);
        }
    }


    public void addProgressListener(QoSMeasurementClientProgressListener listener) {
        progressListeners.add(listener);
    }

    public void removeProgressListener(QoSMeasurementClientProgressListener listener) {
        progressListeners.remove(listener);
    }

    public void addControlListener(QoSMeasurementClientControlListener listener) {
        controlListeners.add(listener);
    }

    public void removeControlListener(QoSMeasurementClientControlListener listener) {
        controlListeners.remove(listener);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void setClient(final ClientHolder client) {
        this.client = client;
    }

    public void setTestSettings(final TestSettings settings) {
        this.testSettings = settings;
    }

    public QualityOfServiceTest getQosTest() {
        return qosTest;
    }
}
