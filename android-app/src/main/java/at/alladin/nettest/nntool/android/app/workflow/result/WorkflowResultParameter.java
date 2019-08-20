package at.alladin.nettest.nntool.android.app.workflow.result;

import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class WorkflowResultParameter implements WorkflowParameter {

    private String measurementUuid;

    public String getMeasurementUuid() {
        return measurementUuid;
    }

    public void setMeasurementUuid(String measurementUuid) {
        this.measurementUuid = measurementUuid;
    }
}
