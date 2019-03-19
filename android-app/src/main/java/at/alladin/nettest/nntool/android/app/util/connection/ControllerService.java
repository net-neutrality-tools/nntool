package at.alladin.nettest.nntool.android.app.util.connection;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public interface ControllerService {

    @POST("api/v1/measurement-agents")
    Call<ApiResponse<RegistrationResponse>> postRegisterClient(@Body final ApiRequest<RegistrationRequest> request);
}
