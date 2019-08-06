package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ControllerConnection extends AbstractConnection<ControllerService> {

    private final String hostname;

    private final String hostname6;

    private final int port;

    public ControllerConnection(final boolean isEncrypted, final String hostname,
                                final String hostname6, final int port, final String pathPrefix) {
        super(isEncrypted, hostname, hostname6, port, pathPrefix, ControllerService.class);
        this.hostname = hostname;
        this.hostname6 = hostname6;
        this.port = port;
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

    public SpeedMeasurementPeerResponse getMeasurementPeers(final ApiRequest<SpeedMeasurementPeerRequest> request) {
        try {
            return getControllerService().getMeasurementPeers().execute().body().getData();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public IpResponse getIpAddress(final IpResponse.IpVersion ipVersion) {
        try {
            switch (ipVersion) {
                case IPv4:
                    return getControllerService().getAgentIpAddress().execute().body().getData();
                case IPv6:
                    return getControllerService6().getAgentIpAddress().execute().body().getData();
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String getHostname() {
        return hostname;
    }

    public String getHostname6() {
        return hostname6;
    }

    public int getPort() {
        return port;
    }
}
