package at.alladin.nettest.nntool.android.app.util;

import android.content.Context;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.nntool.android.app.util.connection.CollectorConnection;
import at.alladin.nettest.nntool.android.app.util.connection.ControllerConnection;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */
public class ConnectionUtil {

    /**
     *
     * @param context
     * @return
     */
    public static ControllerConnection createControllerConnection(final Context context) {
        final String host = context.getResources().getString(R.string.default_controller_host);
        final String pathPrefix = context.getResources().getString(R.string.default_controller_path_prefix);
        final Integer port = context.getResources().getInteger(R.integer.default_controller_port);
        final boolean isEncypted = context.getResources().getBoolean(R.bool.default_controller_connection_is_encrypted);

        return new ControllerConnection(isEncypted, host, null, port, pathPrefix);
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
