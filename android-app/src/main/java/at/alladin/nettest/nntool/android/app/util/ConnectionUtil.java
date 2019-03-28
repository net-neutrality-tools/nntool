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

    public static CollectorConnection createCollectorConnection(final String collectorUrl) {
        return new CollectorConnection(collectorUrl);
    }
}
