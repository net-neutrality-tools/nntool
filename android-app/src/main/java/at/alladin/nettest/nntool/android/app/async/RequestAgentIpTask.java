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

package at.alladin.nettest.nntool.android.app.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import at.alladin.nettest.nntool.android.app.util.ConnectionUtil;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ip.IpResponse;

/**
 * @author Felix Kendlbacher (fk@alladin.at)
 */
public class RequestAgentIpTask extends AsyncTask<Void, Void, Map<IpResponse.IpVersion, RequestAgentIpTask.IpResponseWrapper>> {

    private final static String TAG = RequestAgentIpTask.class.getSimpleName();

    private final Context context;

    private final OnTaskFinishedCallback<Map<IpResponse.IpVersion, RequestAgentIpTask.IpResponseWrapper>> callback;


    public RequestAgentIpTask(final Context context,
                              final OnTaskFinishedCallback<Map<IpResponse.IpVersion, RequestAgentIpTask.IpResponseWrapper>> callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Map<IpResponse.IpVersion, RequestAgentIpTask.IpResponseWrapper> doInBackground(Void... voids) {
        final ControllerConnection controllerConnection = ConnectionUtil.createControllerConnection(context);
        final Map<IpResponse.IpVersion, RequestAgentIpTask.IpResponseWrapper> ret = new HashMap<>();
        Log.d(TAG, "Requesting measurement agent ip");
        for (IpResponse.IpVersion ipVersion : IpResponse.IpVersion.values()) {
            //fetch local address via socket
            final InetAddress ip = getLocalInetAddress(controllerConnection, ipVersion);

            IpResponse response = null;
            try {
                response = controllerConnection.getIpAddress(ipVersion);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (response != null || ip != null) {
                ret.put(ipVersion, new IpResponseWrapper(response, ip));
            }

        }
        return ret;
    }

    @Override
    protected void onPostExecute(final Map<IpResponse.IpVersion, RequestAgentIpTask.IpResponseWrapper> result) {
        if (callback != null) {
            callback.onTaskFinished(result);
        }
    }

    private InetAddress getLocalInetAddress(final ControllerConnection controllerConnection, final IpResponse.IpVersion version) {
        try (Socket s = new Socket()) {
            InetSocketAddress address = null;
            switch (version) {
                case IPv6:
                    address = new InetSocketAddress(controllerConnection.getHostname6(), controllerConnection.getPort());
                    break;
                case IPv4:
                    address = new InetSocketAddress(controllerConnection.getHostname4(), controllerConnection.getPort());
                    break;
            }
            s.connect(address, 2000);
            return s.getLocalAddress();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static class IpResponseWrapper {
        private final IpResponse ipResponse;

        private final InetAddress localAddress;

        public IpResponseWrapper(final IpResponse ipResponse, final InetAddress inetAddress) {
            this.ipResponse = ipResponse;
            this.localAddress = inetAddress;
        }

        public IpResponse getIpResponse() {
            return ipResponse;
        }

        public InetAddress getLocalAddress() {
            return localAddress;
        }
    }

}
