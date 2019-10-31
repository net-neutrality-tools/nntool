package at.alladin.nettest.nntool.android.app.workflow.main;

import at.alladin.nettest.nntool.android.app.workflow.WorkflowParameter;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class WorkflowTitleParameter implements WorkflowParameter {

    private boolean showTermsAndConditionsOnLoad = false;

    public boolean isShowTermsAndConditionsOnLoad() {
        return showTermsAndConditionsOnLoad;
    }

    public void setShowTermsAndConditionsOnLoad(boolean showTermsAndConditionsOnLoad) {
        this.showTermsAndConditionsOnLoad = showTermsAndConditionsOnLoad;
    }
}
