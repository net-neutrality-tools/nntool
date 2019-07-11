package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface ResultService {

    @GET("/api/v1/measurement-agents/{agentUuid}/measurements")
    Call<ApiResponse<ApiPagination<BriefMeasurementResponse>>> getMeasurementsRequest(@Path("agentUuid") String agentUuid);

    @GET("/api/v1/measurement-agents/{agentUuid}/measurements/{measurementUuid}/details")
    Call<ApiResponse<DetailMeasurementResponse>> getGroupedDetailMeasurementRequest(@Path("agentUuid") String agentUuid, @Path("measurementUuid") String measurementUuid);

    @DELETE("/api/v1/measurement-agents/{agentUuid}/measurements/{measurementUuid}")
    Call<ApiResponse<DisassociateResponse>> deleteSingleMeasurement(@Path("agentUuid") String agentUuid, @Path("measurementUuid") String measurementUuid);
}
