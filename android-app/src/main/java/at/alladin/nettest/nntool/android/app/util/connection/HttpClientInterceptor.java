package at.alladin.nettest.nntool.android.app.util.connection;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class HttpClientInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        builder.addHeader("Accept-Language", Locale.getDefault().getLanguage());
        Request request = builder.build();
        Response response = chain.proceed(request);

        return response;
    }
}
