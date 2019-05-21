package at.alladin.nettest.nntool.android.app.workflow.measurement.jni;

import android.support.annotation.Keep;
import android.util.Log;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class JniSpeedMeasurementClient {

    static {
        System.loadLibrary("ias-client");
    }

    private static final String TAG = "SPEED_MEASUREMENT_JNI";

    private JniSpeedMeasurementState measurementState;

    public JniSpeedMeasurementClient() {
        measurementState = new JniSpeedMeasurementState();
        shareMeasurementState(measurementState.getDownloadMeasurement(), measurementState.getUploadMeasurement());
    }

    @Keep
    public void cppCallback(final String message) {
        Log.d(TAG, message);
        //Log.d(TAG, "measurement dl throughput " + measurementState.getDownloadMeasurement().getThroughputAvgBps() + " time: " + measurementState.getDownloadMeasurement().getDurationMsTotal());
    }

    /*
            AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                startMeasurement();
            }
        });
     */

    public native void startMeasurement();

    public native void stopMeasurement();

    /**
     * Call this method before starting a test to allow the cpp impl to write the current state into the passed JniSpeedMeasurementState obj
     * Is automatically called for the devs in the constructor
     */
    private native void shareMeasurementState(final JniSpeedMeasurementState.JniSpeedState downloadMeasurementState, final JniSpeedMeasurementState.JniSpeedState uploadMeasurementState);
}
