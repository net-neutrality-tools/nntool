package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface CollectorService {

    @POST
    Call<ApiResponse<MeasurementResultResponse>> postMeasurementRequest(@Body final LmapReportDto request, @Url String urlSuffix);
}
