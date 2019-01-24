package at.alladin.nettest.qos;

import at.alladin.nettest.shared.model.qos.QosMeasurementType;
import at.alladin.nntool.client.v2.task.QoSTestEnum;

public abstract class QoSMeasurementClientProgressAdapter implements QoSMeasurementClientProgressListener {

    @Override
    public void onProgress(float progress) {

    }

    @Override
    public void onQoSTypeProgress(QosMeasurementType qosType, float progress) {

    }

    @Override
    public void onQoSTypeStarted(QosMeasurementType qosType) {

    }

    @Override
    public void onQoSTypeFinished(QosMeasurementType qosType) {

    }

    @Override
    public void onQoSStatusChanged(QoSTestEnum newStatus) {

    }

    @Override
    public void onQoSTestsDefined(int testCount) {

    }
}
