package at.alladin.rmbt.client.ndt;

import at.alladin.rmbt.client.helper.NdtStatus;

/**
 * Created by fk on 2/22/18.
 */

public interface NdtRunner {

    float getNdtProgress();

    NdtStatus getNdtStatus();

    void setNdtCancelled(final boolean cancelled);

    void run();

    void setUiServices(final BaseNdtRunner.UiServices uiServices);

    void setNdtNetworkType(final String ndtNetworkType);
}
