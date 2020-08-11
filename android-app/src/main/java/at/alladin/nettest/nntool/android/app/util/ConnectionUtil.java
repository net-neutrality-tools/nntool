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

package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.connection.CollectorConnection;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;
import at.alladin.nettest.nntool.android.app.util.connection.MapConnection;
import at.alladin.nettest.nntool.android.app.util.connection.ResultConnection;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ConnectionUtil {

    /**
     *
     * @param context
     * @return
     */
    public static ResultConnection createResultConnection(final Context context) {
        final boolean overrideSettings = context.getResources().getBoolean(R.bool.default_result_service_settings_override);
        final String resultServiceUrl = PreferencesUtil.getResultServiceUrl(context);

        if (overrideSettings || resultServiceUrl == null) {
            final String host = context.getResources().getString(R.string.default_result_service_host);
            final String pathPrefix = context.getResources().getString(R.string.default_result_service_path_prefix);
            final Integer port = context.getResources().getInteger(R.integer.default_result_service_port);
            final boolean isEncypted = context.getResources().getBoolean(R.bool.default_result_service_connection_is_encrypted);

            return new ResultConnection(isEncypted, host, port, pathPrefix);
        }

        return new ResultConnection(resultServiceUrl);
    }

    public static MapConnection createMapConnection (final Context context) {
        final boolean overrideSettings = context.getResources().getBoolean(R.bool.default_result_service_settings_override);
        final String mapServiceUrl = PreferencesUtil.getMapServiceUrl(context);

        if (overrideSettings || mapServiceUrl == null) {
            final String host = context.getResources().getString(R.string.default_map_service_host);
            final String pathPrefix = context.getResources().getString(R.string.default_map_service_path_prefix);
            final Integer port = context.getResources().getInteger(R.integer.default_map_service_port);
            final boolean isEncypted = context.getResources().getBoolean(R.bool.default_map_service_connection_is_encrypted);

            return new MapConnection(isEncypted, host, port, pathPrefix);
        }

        return new MapConnection(mapServiceUrl);
    }


    /**
     *
     * @param context
     * @return
     */
    public static ControllerConnection createControllerConnection(final Context context) {
        final String host = context.getResources().getString(R.string.default_controller_host);
        final String hostv4 = context.getResources().getString(R.string.default_controller_host_ipv4);
        final String pathPrefix = context.getResources().getString(R.string.default_controller_path_prefix);
        final Integer port = context.getResources().getInteger(R.integer.default_controller_port);
        final boolean isEncypted = context.getResources().getBoolean(R.bool.default_controller_connection_is_encrypted);
        final String hostV6 = context.getResources().getString(R.string.default_controller_host_ipv6);

        return new ControllerConnection(isEncypted, host, hostv4, hostV6, port, pathPrefix);
    }

    /**
     *
     * @param context
     * @param collectorUrl
     * @return
     */
    public static CollectorConnection createCollectorConnection(final Context context, final String collectorUrl) {
        final boolean overrideCollectorSettings = context.getResources().getBoolean(R.bool.default_collector_settings_override);
        if (collectorUrl != null && !overrideCollectorSettings) {
            return new CollectorConnection(collectorUrl);
        }

        final String host = context.getResources().getString(R.string.default_collector_host);
        final String pathPrefix = context.getResources().getString(R.string.default_collector_path_prefix);
        final int port = context.getResources().getInteger(R.integer.default_collector_port);
        final boolean isEncrypted = context.getResources().getBoolean(R.bool.default_collector_connection_is_encrypted);

        return new CollectorConnection(isEncrypted, host, port, pathPrefix);
    }
}
