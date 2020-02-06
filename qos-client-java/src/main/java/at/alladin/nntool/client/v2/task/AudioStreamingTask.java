package at.alladin.nntool.client.v2.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import at.alladin.nntool.client.ClientHolder;
import at.alladin.nntool.client.QualityOfServiceTest;
import at.alladin.nntool.client.v2.task.result.QoSTestResult;
import at.alladin.nntool.client.v2.task.service.AudioStreamingService;
import at.alladin.nntool.client.v2.task.service.TestSettings;
import at.alladin.nntool.shared.qos.QosMeasurementType;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class AudioStreamingTask extends AbstractQoSTask {

    public final static long DEFAULT_TIMEOUT = 30000000000L;

    public final static long DEFAULT_TARGET_PLAYBACK_DURATION_MS = 12000L;

    public final static long DEFAULT_TARGET_BUFFER_DURATION_NS = 200000000L;

    public final static String PARAM_TIMEOUT = "timeout";

    public final static String PARAM_BUFFER_DURATION = "param_buffer_duration";

    public final static String PARAM_TARGET_PLAYBACK_DURATION_MS = "param_target_playback_duration_ms";

    public final static String PARAM_TARGET_URL = "param_target_url";

    public final static String RESULT_DETAILS = "audio_streaming_result";

    public final static String RESULT_AUDIO_START_TIME = "audio_start_time_ns";

    public final static String RESULT_STALLS = "stalls";

    public final static String RESULT_STATUS = "audio_streaming_status";

    public final static String RESULT_OBJECTIVE_URL = "audio_streaming_objective_target_url";

    public final static String RESULT_OBJECTIVE_PLAYBACK_DURATION = "audio_streaming_objective_playback_duration_ms";

    private final long timeout;

    private final String targetUrl;

    private final long bufferDuration;

    private final long playbackDurationMs;

    public AudioStreamingTask(QualityOfServiceTest nnTest, TaskDesc taskDesc, int threadId) {
        super(nnTest, taskDesc, threadId, threadId);

        Object value = taskDesc.getParams().get(PARAM_TIMEOUT);
        this.timeout = value != null ? Long.valueOf((String) value) : DEFAULT_TIMEOUT;

        targetUrl = (String) taskDesc.getParams().get(PARAM_TARGET_URL);

        value = taskDesc.getParams().get(PARAM_BUFFER_DURATION);
        this.bufferDuration = value != null ? Long.valueOf((String) value) : DEFAULT_TARGET_BUFFER_DURATION_NS;

        value = taskDesc.getParams().get(PARAM_TARGET_PLAYBACK_DURATION_MS);
        this.playbackDurationMs = value != null ? Long.valueOf((String) value) : DEFAULT_TARGET_PLAYBACK_DURATION_MS;

    }

    @Override
    public void initTask() {

    }

    @Override
    public QosMeasurementType getTestType() {
        return QosMeasurementType.AUDIO_STREAMING;
    }

    @Override
    public boolean needsQoSControlConnection() {
        return false;
    }

    @Override
    public QoSTestResult call() throws Exception {
        final QoSTestResult testResult = initQoSTestResult(QosMeasurementType.AUDIO_STREAMING);
        if (targetUrl == null) {
            testResult.setFatalError(true);
            return testResult;
        }

        try {
            onStart(testResult);
            testResult.getResultMap().put(RESULT_OBJECTIVE_PLAYBACK_DURATION, this.playbackDurationMs);
            testResult.getResultMap().put(RESULT_OBJECTIVE_URL, this.targetUrl);

            final TestSettings settings = getQoSTest().getTestSettings();

            final AudioStreamingService audioStreamingService = settings.getAudioStreamingService();
            audioStreamingService.setBufferDuration(this.bufferDuration);
            audioStreamingService.setTargetPlaybackDuration(this.playbackDurationMs);
            audioStreamingService.setTargetUrl(this.targetUrl);

            AudioStreamingService.AudioStreamingResult result = null;

            final Future<AudioStreamingService.AudioStreamingResult> audioStreamingFuture = ClientHolder.getCommonThreadPool().submit(audioStreamingService);
            try {
                result = audioStreamingFuture.get(timeout, TimeUnit.NANOSECONDS);
                testResult.getResultMap().put(RESULT_STATUS, "OK");
            }
            catch (TimeoutException e) {
                testResult.getResultMap().put(RESULT_STATUS, "TIMEOUT");
            }
            finally {
                if (result != null) {
                    JSONObject jsonResult = result.toJson();
                    try {
                        if (jsonResult.has(RESULT_AUDIO_START_TIME)) {
                            testResult.getResultMap().put(RESULT_AUDIO_START_TIME, jsonResult.getLong(RESULT_AUDIO_START_TIME));
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        if (jsonResult.has(RESULT_STALLS)) {
                            JSONArray stallArray = jsonResult.getJSONArray(RESULT_STALLS);
                            final List<Long> stalls = new ArrayList<>();
                            for (int i = 0; i < stallArray.length(); i++) {
                                stalls.add(stallArray.getLong(i));
                            }
                            testResult.getResultMap().put(RESULT_STALLS, stalls);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    testResult.getResultMap().put(RESULT_DETAILS, result.toString());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            testResult.getResultMap().put(RESULT_STATUS, "ERROR");
        }
        finally {
            onEnd(testResult);
        }

        return testResult;
    }
}
