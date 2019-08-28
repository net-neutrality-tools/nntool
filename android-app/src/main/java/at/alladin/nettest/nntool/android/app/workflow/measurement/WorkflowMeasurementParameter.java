package at.alladin.nettest.nntool.android.app.workflow.measurement;

import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class WorkflowMeasurementParameter implements WorkflowParameter {

    private boolean isSpeedEnabled;

    private boolean isQoSEnabled;

    public boolean isSpeedEnabled() {
        return isSpeedEnabled;
    }

    public void setSpeedEnabled(boolean speedEnabled) {
        isSpeedEnabled = speedEnabled;
    }

    public boolean isQoSEnabled() {
        return isQoSEnabled;
    }

    public void setQoSEnabled(boolean qoSEnabled) {
        isQoSEnabled = qoSEnabled;
    }
}
