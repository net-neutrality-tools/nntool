package at.alladin.nettest.nntool.android.app.util.connection;

import java.util.stream.Collectors;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class CollectorConnection extends AbstractConnection<CollectorService> {

    public CollectorConnection(final String url) {
        super(url, null, CollectorService.class);
    }

    public CollectorConnection(final boolean isEncrypted, final String hostname, final int port, final String pathPrefix) {
        super(isEncrypted, hostname, null, port, pathPrefix, CollectorService.class);
    }

    public MeasurementResultResponse sendMeasurementReport(final LmapReportDto reportDto) {
        try {
            return getControllerService().postMeasurementRequest(reportDto, "").execute().body().getData();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
