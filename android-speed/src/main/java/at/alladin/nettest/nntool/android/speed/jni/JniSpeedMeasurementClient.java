package at.alladin.nettest.nntool.android.speed.jni;

import android.support.annotation.Keep;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nettest.nntool.android.speed.JniSpeedMeasurementResult;
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

    private final SpeedTaskDesc speedTaskDesc;

    private List<MeasurementFinishedStringListener> finishedStringListeners = new ArrayList<>();

    private List<MeasurementFinishedListener> finishedListeners = new ArrayList<>();

    private List<MeasurementPhaseListener> measurementPhaseListeners = new ArrayList<>();

    private SpeedMeasurementState.MeasurementPhase previousMeasurementPhase = SpeedMeasurementState.MeasurementPhase.INIT;

    public JniSpeedMeasurementClient(final SpeedTaskDesc speedTaskDesc) {
        this.speedTaskDesc = speedTaskDesc;
        speedMeasurementState = new SpeedMeasurementState();
        shareMeasurementState(speedTaskDesc, speedMeasurementState, speedMeasurementState.getPingMeasurement(), speedMeasurementState.getDownloadMeasurement(), speedMeasurementState.getUploadMeasurement());
    }

    @Keep
    public void cppCallback(final String message) {
        if (previousMeasurementPhase != speedMeasurementState.getMeasurementPhase()) {
            Log.d(TAG, "Previous state: " + previousMeasurementPhase.toString() + " current state: " + speedMeasurementState.getMeasurementPhase().toString());
            for(MeasurementPhaseListener l : measurementPhaseListeners) {
                l.onMeasurementPhaseFinished(previousMeasurementPhase);
                l.onMeasurementPhaseStarted(speedMeasurementState.getMeasurementPhase());
            }
            previousMeasurementPhase = speedMeasurementState.getMeasurementPhase();
        }
        Log.d(TAG, message);
    }

    @Keep
    public void cppCallbackFinished (final String message, final JniSpeedMeasurementResult result) {
        Log.d(TAG, message);
        for (MeasurementFinishedStringListener l : finishedStringListeners) {
            l.onMeasurementFinished(message);
        }
        for (MeasurementFinishedListener l : finishedListeners) {
            l.onMeasurementFinished(result, speedTaskDesc);
        }
        Log.d(TAG, result.toString());
    }

    public SpeedMeasurementState getSpeedMeasurementState() {
        return speedMeasurementState;
    }

    public native void startMeasurement() throws AndroidJniCppException;

    public native void stopMeasurement() throws AndroidJniCppException;

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

    public void addMeasurementPhaseListener (final MeasurementPhaseListener listener) {
        measurementPhaseListeners.add(listener);
    }

    public void removeMeasurementPhaseListener (final MeasurementPhaseListener listener) {
        measurementPhaseListeners.remove(listener);
    }

    public String getCollectorUrl() {
        return collectorUrl;
    }

    public void setCollectorUrl(String collectorUrl) {
        this.collectorUrl = collectorUrl;
    }

    public interface MeasurementFinishedStringListener {

        void onMeasurementFinished (final String result);

    }

    public interface MeasurementFinishedListener {

        void onMeasurementFinished (final JniSpeedMeasurementResult result, final SpeedTaskDesc taskDesc);
    }

    public interface MeasurementPhaseListener {
        void onMeasurementPhaseStarted (final SpeedMeasurementState.MeasurementPhase startedPhase);

        void onMeasurementPhaseFinished (final SpeedMeasurementState.MeasurementPhase finishedPhase);
    }
}
