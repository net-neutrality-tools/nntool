package at.alladin.nettest.qos;

import java.util.List;

import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.rmbt.client.v2.task.result.QoSResultCollector;

public abstract class QoSMeasurementClientControlAdapter implements QoSMeasurementClientControlListener {

    @Override
    public void onMeasurementStarted(List<QosMeasurementType> testsToBeExecuted) {

    }

    @Override
    public void onMeasurementStopped() {

    }

    @Override
    public void onMeasurementError(Exception e) {

    }

    @Override
    public void onMeasurementFinished(String qosTestUuid, QoSResultCollector qoSResultCollector) {

    }
}
