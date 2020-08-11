/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.nntool.android.app.util.connection;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.alladin.nettest.nntool.android.app.util.ObjectMapperUtil;
import at.alladin.nettest.nntool.android.app.util.PreferencesUtil;
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

    //will be used for IPv4 only requests
    T controllerService4;

    //will be used for IPv6 only requests
    T controllerService6;

    public AbstractConnection(final boolean isEncrypted, final String hostname, final String hostname4,
                              final String hostname6, final int port, final String pathPrefix,
                              final Class<T> serviceClazz) {
        this ((isEncrypted ? "https://" : "http://") + hostname + ":" + port + pathPrefix,
                (isEncrypted ? "https://" : "http://") + hostname4 + ":" + port + pathPrefix,
                (isEncrypted ? "https://" : "http://") + hostname6 + ":" + port + pathPrefix, serviceClazz);
    }

    public AbstractConnection(final String url, final String url4, final String url6, final Class<T> serviceClazz) {
        final List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpClientInterceptor())
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .protocols(protocols)
                .build();

        try {
            Retrofit r = new Retrofit.Builder()
                    .baseUrl(appendTrailingSlashToUrl(url))
                    .addConverterFactory(JacksonConverterFactory.create(ObjectMapperUtil.createBasicObjectMapper()))
                    .client(httpClient)
                    .build();

            System.out.println("BASE URL: " + r.baseUrl().toString());
            controllerService = r.create(serviceClazz);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        try {
            Retrofit r4 = new Retrofit.Builder()
                    .baseUrl(appendTrailingSlashToUrl(url4 == null ? url : url4))
                    .addConverterFactory(JacksonConverterFactory.create(ObjectMapperUtil.createBasicObjectMapper()))
                    .client(httpClient)
                    .build();

            System.out.println("BASE URL v4: " + r4.baseUrl().toString());
            controllerService4 = r4.create(serviceClazz);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }

        try {
            Retrofit r6 = new Retrofit.Builder()
                    .baseUrl(appendTrailingSlashToUrl(url6 == null ? url : url6))
                    .addConverterFactory(JacksonConverterFactory.create(ObjectMapperUtil.createBasicObjectMapper()))
                    .client(httpClient)
                    .build();

            System.out.println("BASE URL v6: " + r6.baseUrl().toString());
            controllerService6 = r6.create(serviceClazz);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public T getControllerService() {
        return controllerService;
    }

    public T getControllerService6() {
        return controllerService6;
    }

    public T getControllerService4() {
        return controllerService4;
    }

    public T getPreferredControllerService(final Context context) {
        return PreferencesUtil.isForceIpv4(context) ? controllerService4 : controllerService;
    }

    private String appendTrailingSlashToUrl(final String url) {
        return (url == null || url.length() == 0 || url.charAt(url.length() - 1) == '/') ?
                url : url + "/";
    }
}
