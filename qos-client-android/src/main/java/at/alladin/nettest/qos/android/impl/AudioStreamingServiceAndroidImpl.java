package at.alladin.nettest.qos.android.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.freedesktop.gstreamer.GStreamer;
import org.json.JSONException;
import org.json.JSONObject;

import at.alladin.nntool.client.v2.task.service.AudioStreamingService;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class AudioStreamingServiceAndroidImpl implements AudioStreamingService {

    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("qos-cpp");
    }

    private native void nativeInit(String resourceUrl, long bufferDurationNs, long playbackDurationNs);     // Initialize native code, build pipeline, etc
    private native void nativeFinalize(); // Destroy pipeline and shutdown native code
    private native String nativeStartTest();

    private final Context context;

    private final static String TAG = AudioStreamingServiceAndroidImpl.class.getSimpleName();

    private String targetUrl;

    private long bufferDurationNs;

    private long playbackDurationMs;

    public AudioStreamingServiceAndroidImpl(final Context context) {
        this.context = context;
    }

    public void stop() {
        nativeFinalize();
    }

    @Override
    public AudioStreamingResult call() throws Exception {

        try {
            GStreamer.init(context);
        } catch (Exception e) {
            //TODO: provide error result
            return null;
        }
        nativeInit(this.targetUrl, this.bufferDurationNs, this.playbackDurationNs);
        return new AudioStreamingResultAndroidImpl(nativeStartTest());
    }

    @Override
    public void setTargetUrl(String url) {
        this.targetUrl = url;
    }

    @Override
    public void setBufferDuration(long durationNs) {
        this.bufferDurationNs = durationNs;
    }

    @Override
    public void setTargetPlaybackDuration(long playbackDurationNs) {
        this.playbackDurationNs = playbackDurationNs;
    }

    public final static class AudioStreamingResultAndroidImpl implements AudioStreamingService.AudioStreamingResult {

        private String result;

        public AudioStreamingResultAndroidImpl (final String result) {
            this.result = result;
        }

        @Override
        public JSONObject toJson() {
            try {
                return new JSONObject(result);
            } catch (JSONException ex) {
                Log.i(TAG, result);
                Log.e(TAG, ex.getMessage());
                return null;
            }
        }

        @Override
        public String toString() {
            return result;
        }
    }
}
