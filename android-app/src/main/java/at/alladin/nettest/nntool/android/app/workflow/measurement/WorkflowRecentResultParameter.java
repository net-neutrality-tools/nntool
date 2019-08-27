package at.alladin.nettest.nntool.android.app.workflow.measurement;

import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class WorkflowRecentResultParameter implements WorkflowParameter {

    private String recentResultUuid;

    private String recentResultOpenDataUuid;

    public String getRecentResultUuid() {
        return recentResultUuid;
    }

    public void setRecentResultUuid(String recentResultUuid) {
        this.recentResultUuid = recentResultUuid;
    }

    public String getRecentResultOpenDataUuid() {
        return recentResultOpenDataUuid;
    }

    public void setRecentResultOpenDataUuid(String recentResultOpenDataUuid) {
        this.recentResultOpenDataUuid = recentResultOpenDataUuid;
    }
}
