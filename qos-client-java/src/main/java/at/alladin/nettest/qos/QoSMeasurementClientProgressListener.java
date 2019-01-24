package at.alladin.nettest.qos;


import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.v2.task.QoSTestEnum;

public interface QoSMeasurementClientProgressListener {

    /**
     * Register for overall progress events
     * @param progress [0, 1]
     */
    public void onProgress(float progress);

    /**
     * On progress for the specific qosTypes
     * @param progress [0, 1]
     */
    public void onQoSTypeProgress(QosMeasurementType qosType, float progress);

//    /**
//     * On progress for the single qosTests
//     * @param event
//     */
//    public void onQoSTestProgress(Event event);

    /**
     * Called when the first test of the QosMeasurementType is started
     * @param qosType
     */
    public void onQoSTypeStarted(QosMeasurementType qosType);

    /**
     * Called when the last test of the QosMeasurementType has ended
     * @param qosType
     */
    public void onQoSTypeFinished(QosMeasurementType qosType);

    /**
     * Called when a new qosTestStatus has been reached (e.g. QoS_RUNNING when the first QoS test is executed)
     * @param newStatus
     */
    public void onQoSStatusChanged(QoSTestEnum newStatus);

    /**
     * Called when the qos tests have been defined
     * @param testCount the total # of qos tests to be executed
     */
    public void onQoSTestsDefined(int testCount);

//    public void onQoSTestStarted(Event event);

//    public void onQoSTestFinished(Event event);

}
