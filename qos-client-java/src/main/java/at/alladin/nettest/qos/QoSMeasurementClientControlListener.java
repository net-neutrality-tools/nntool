package at.alladin.nettest.qos;

import java.util.List;

import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.rmbt.client.v2.task.result.QoSResultCollector;

public interface QoSMeasurementClientControlListener {

    public void onMeasurementStarted(List<QosMeasurementType> testsToBeExecuted);

    /**
     * If the user stopped the test
     */
    public void onMeasurementStopped();

    /**
     * If the qos test ran into an error
     */
    public void onMeasurementError(Exception e);

    /**
     * If the qos test finished naturally
     * @param
     */
    public void onMeasurementFinished(String qostTestUuid, QoSResultCollector resultCollector);
}
