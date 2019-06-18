package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ControllerConnection extends AbstractConnection<ControllerService> {

    public ControllerConnection(final boolean isEncrypted, final String hostname,
                                final String hostname6, final int port, final String pathPrefix) {
        super(isEncrypted, hostname, hostname6, port, pathPrefix, ControllerService.class);
    }

    public RegistrationResponse registerMeasurementAgent(final ApiRequest<RegistrationRequest> request) {
        try {
            return getControllerService().postRegisterClient(request).execute().body().getData();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public LmapControlDto requestMeasurement (final LmapControlDto request) {
        try {
            return getControllerService().postMeasurementRequest(request).execute().body();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
