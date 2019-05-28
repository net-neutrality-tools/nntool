package at.alladin.nettest.nntool.android.app.workflow.measurement.jni;

import android.support.annotation.Keep;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.app.workflow.measurement.SpeedMeasurementState;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class JniSpeedMeasurementClient {

    static {
        System.loadLibrary("ias-client");
    }

    private static final String TAG = "SPEED_MEASUREMENT_JNI";

    private SpeedMeasurementState speedMeasurementState;

    public JniSpeedMeasurementClient() {
        speedMeasurementState = new SpeedMeasurementState();
        shareMeasurementState(speedMeasurementState, speedMeasurementState.getPingMeasurement(), speedMeasurementState.getDownloadMeasurement(), speedMeasurementState.getUploadMeasurement());
    }

    @Keep
    public void cppCallback(final String message) {
        Log.d(TAG, message);
    }

    @Keep
    public void cppCallbackFinished (final String message) {
        Log.d(TAG, message);
    }

    public SpeedMeasurementState getSpeedMeasurementState() {
        return speedMeasurementState;
    }

    public native void startMeasurement();

    public native void stopMeasurement();

    /**
     * Call this method before starting a test to allow the cpp impl to write the current state into the passed JniSpeedMeasurementState obj
     * Is automatically called for the devs in the constructor
     */
    private native void shareMeasurementState(final SpeedMeasurementState speedMeasurementState, final SpeedMeasurementState.PingPhaseState pingMeasurementState,
                                              final SpeedMeasurementState.SpeedPhaseState downloadMeasurementState, final SpeedMeasurementState.SpeedPhaseState uploadMeasurementState);

    /**
     * Method to free the global java references from the cpp code
     * Call after every ended measurement (either stopped or finished)
     * Equivalent to shareMeasurementState method
     */
    private native void cleanUp();
}
