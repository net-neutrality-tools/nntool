package at.alladin.nettest.nntool.android.speed.jni;

import android.support.annotation.Keep;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.speed.SpeedMeasurementResult;
import at.alladin.nettest.nntool.android.speed.SpeedMeasurementState;
import at.alladin.nettest.nntool.android.speed.SpeedTaskDesc;
import at.alladin.nettest.nntool.android.speed.jni.exception.AndroidJniCppException;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class JniSpeedMeasurementClient {

    static {
        System.loadLibrary("ias-client");
    }

    private static final String TAG = "SPEED_MEASUREMENT_JNI";

    private SpeedMeasurementState speedMeasurementState;

    private String collectorUrl;

    private SpeedTaskDesc speedTaskDesc;

    private List<MeasurementFinishedStringListener> finishedStringListeners = new ArrayList<>();

    private List<MeasurementFinishedListener> finishedListeners = new ArrayList<>();

    public JniSpeedMeasurementClient(final SpeedTaskDesc speedTaskDesc) {
        this.speedTaskDesc = speedTaskDesc;
        speedMeasurementState = new SpeedMeasurementState();
        shareMeasurementState(speedTaskDesc, speedMeasurementState, speedMeasurementState.getPingMeasurement(), speedMeasurementState.getDownloadMeasurement(), speedMeasurementState.getUploadMeasurement());
    }

    @Keep
    public void cppCallback(final String message) {
        Log.d(TAG, message);
    }

    @Keep
    public void cppCallbackFinished (final String message) {
        Log.d(TAG, message);
        for (MeasurementFinishedStringListener l : finishedStringListeners) {
            l.onMeasurementFinished(message);
        }
        for (MeasurementFinishedListener l : finishedListeners) {
            l.onMeasurementFinished(null);
        }
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
    private native void shareMeasurementState(final SpeedTaskDesc speedTaskDesc, final SpeedMeasurementState speedMeasurementState, final SpeedMeasurementState.PingPhaseState pingMeasurementState,
                                              final SpeedMeasurementState.SpeedPhaseState downloadMeasurementState, final SpeedMeasurementState.SpeedPhaseState uploadMeasurementState);

    public void addMeasurementFinishedListener(final MeasurementFinishedStringListener listener) {
        finishedStringListeners.add(listener);
    }

    public void addMeasurementFinishedListener(final MeasurementFinishedListener listener) {
        finishedListeners.add(listener);
    }

    public void removeMeasurementFinishedListener(final MeasurementFinishedStringListener listener) {
        finishedStringListeners.remove(listener);
    }

    public void removeMeasurementFinishedListener(final MeasurementFinishedListener listener) {
        finishedListeners.remove(listener);
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public void setCollectorUrl(String collectorUrl) {
        this.collectorUrl = collectorUrl;
    }

    public SpeedTaskDesc getSpeedTaskDesc() {
        return speedTaskDesc;
    }

    public void setSpeedTaskDesc(SpeedTaskDesc speedTaskDesc) {
        this.speedTaskDesc = speedTaskDesc;
    }

    public interface MeasurementFinishedStringListener {

        void onMeasurementFinished (final String result);

    }

    public interface MeasurementFinishedListener {

        void onMeasurementFinished (final SpeedMeasurementResult result);
    }
}
