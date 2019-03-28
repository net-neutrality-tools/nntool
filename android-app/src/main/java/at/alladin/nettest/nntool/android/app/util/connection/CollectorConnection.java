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

    private final static int DEFAULT_COLLECTOR_PORT = 8081;

    private final static boolean DEFAULT_COLLECTOR_HAS_ENCRYPTION = false;

    public CollectorConnection(final String hostname) {
        super(DEFAULT_COLLECTOR_HAS_ENCRYPTION, hostname, null,
                DEFAULT_COLLECTOR_PORT, "/", CollectorService.class);
    }

    public MeasurementResultResponse sendMeasurementReport(final LmapReportDto reportDto) {
        try {
            return getControllerService().postMeasurementRequest(reportDto).execute().body().getData();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
