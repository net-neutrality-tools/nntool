package at.alladin.nettest.nntool.android.app.workflow.measurement;

/**
 * @author Lukasz Budryk (lb@alladin.at)
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import at.alladin.nettest.nntool.android.app.R;
import at.alladin.nettest.qos.android.QoSMeasurementClientAndroid;
import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.v2.task.TaskDesc;

/**
 * @author Lukasz Budryk (alladin-IT GmbH)
 */
public class MeasurementService extends Service {

    final static String TAG = MeasurementService.class.getSimpleName();

    public static String ACTION_START_SPEED_MEASUREMENT = "at.alladin.nettest.nntool.android.app.startSpeedMeasurement";

    public static String ACTION_START_QOS_MEASUREMENT = "at.alladin.nettest.nntool.android.app.startQosMeasurement";

    public static String EXTRAS_KEY_QOS_TASK_DESK_LIST = "qos_task_desk_list";

    public static String EXTRAS_KEY_QOS_TASK_COLLECTOR_URL = "qos_task_collector_url";

    final MeasurementServiceBinder binder = new MeasurementServiceBinder();

    final AtomicBoolean isBound = new AtomicBoolean(false);

    QoSMeasurementClientAndroid qosMeasurementClient;

    public class MeasurementServiceBinder extends Binder {
        public MeasurementService getService() {
            return MeasurementService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BIND " + intent);
        this.isBound.set(true);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "UNBIND " + intent);
        this.isBound.set(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "REBIND " + intent);
        this.isBound.set(true);
        super.onRebind(intent);
    }

    public QoSMeasurementClientAndroid getQosMeasurementClient() {
        return qosMeasurementClient;
    }

    public void startMeasurement() {
        //TODO: speed measurement
    }

    public void startQosMeasurement(final Bundle options) {
        //TODO: remove & replace
        /*
        ClientHolder client = ClientHolder.getInstance(getResources().getString(R.string.default_qos_control_host),
                Integer.toString(getResources().getInteger(R.integer.default_qos_control_port)),
                getResources().getIntArray(R.array.qos_tcp_test_port_list),
                getResources().getIntArray(R.array.qos_udp_test_port_list),
                getResources().getString(R.string.qos_echo_service_host),
                getResources().getIntArray(R.array.qos_echo_service_tcp_ports),
                getResources().getIntArray(R.array.qos_echo_service_udp_ports));
                */

        final List<TaskDesc> taskDescList = (List<TaskDesc>) options.getSerializable(EXTRAS_KEY_QOS_TASK_DESK_LIST);
        final String collectorUrl = options.getString(EXTRAS_KEY_QOS_TASK_COLLECTOR_URL);
        final ClientHolder client = ClientHolder.getInstance(taskDescList, collectorUrl);
        qosMeasurementClient = new QoSMeasurementClientAndroid(client, getApplicationContext());
        qosMeasurementClient.start();
    }

    public void cancelMeasurement() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Log.d(TAG, "Got intent with action: '" + intent.getAction() + "'");
            if (ACTION_START_SPEED_MEASUREMENT.equals(intent.getAction())) {
                startMeasurement();
                return START_STICKY;
            }
            else if (ACTION_START_QOS_MEASUREMENT.equals(intent.getAction())) {
                startQosMeasurement(intent.getExtras());
                return START_STICKY;
            }
        }

        return START_NOT_STICKY;
    }
}