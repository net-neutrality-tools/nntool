package at.alladin.nettest.nntool.android.app.util.connection;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ControllerConnection {

    ControllerService controllerService;

    ControllerService controllerService6;

    public ControllerConnection(final boolean isEncrypted, final String hostname, final String hostname6, final int port, final String pathPrefix) {
        final List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpClientInterceptor())
                .protocols(protocols)
                .build();

        Retrofit r = new Retrofit.Builder()
                .baseUrl((isEncrypted ? "https://" : "http://") + hostname + ":" + port + pathPrefix)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();

        System.out.println("BASE URL: " + r.baseUrl().toString());
        controllerService = r.create(ControllerService.class);


        Retrofit r6 = new Retrofit.Builder()
                .baseUrl((isEncrypted ? "https://" : "http://") + hostname6 + ":" + port + pathPrefix)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();

        System.out.println("BASE URL v6: " + r6.baseUrl().toString());
        controllerService6 = r6.create(ControllerService.class);
    }

    public RegistrationResponse registerMeasurementAgent(final ApiRequest<RegistrationRequest> request) {
        request.getData().setGroupName("WasIsDirEgal?");

        try {
            return controllerService.postRegisterClient(request).execute().body().getData();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
