package at.alladin.nettest.nntool.android.app.util.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public abstract class AbstractConnection<T> {

    private final static int DEFAULT_CONNECT_TIMEOUT = 20000; //20ms

    T controllerService;

    //will be used for IPv6 only requests
    T controllerService6;

    public AbstractConnection(final boolean isEncrypted, final String hostname,
                              final String hostname6, final int port, final String pathPrefix,
                              final Class<T> serviceClazz) {
        this ((isEncrypted ? "https://" : "http://") + hostname + ":" + port + pathPrefix,
                (isEncrypted ? "https://" : "http://") + hostname6 + ":" + port + pathPrefix, serviceClazz);
    }

    public AbstractConnection(final String url, final String url6, final Class<T> serviceClazz) {
        final List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpClientInterceptor())
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .protocols(protocols)
                .build();

        Retrofit r = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();

        System.out.println("BASE URL: " + r.baseUrl().toString());
        controllerService = r.create(serviceClazz);


        Retrofit r6 = new Retrofit.Builder()
                .baseUrl(url6 == null ? url : url6)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient)
                .build();

        System.out.println("BASE URL v6: " + r6.baseUrl().toString());
        controllerService6 = r6.create(serviceClazz);
    }

    public T getControllerService() {
        return controllerService;
    }

    public T getControllerService6() {
        return controllerService6;
    }
}
