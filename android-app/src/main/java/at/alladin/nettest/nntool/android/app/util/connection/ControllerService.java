package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface ControllerService {

    @POST("api/v1/measurement-agents")
    Call<ApiResponse<RegistrationResponse>> postRegisterClient(@Body final ApiRequest<RegistrationRequest> request);

    @POST("api/v1/measurements")
    Call<LmapControlDto> postMeasurementRequest(@Body final LmapControlDto request);

    @GET("api/v1/speed-measurement-peers")
    Call<ApiResponse<SpeedMeasurementPeerResponse>> getMeasurementPeers();

    @GET("api/v1/ip")
    Call<ApiResponse<IpResponse>> getAgentIpAddress();
}
