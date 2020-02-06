package at.alladin.nntool.client.v2.task.service;

import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public interface AudioStreamingService extends Callable<AudioStreamingService.AudioStreamingResult> {

    interface AudioStreamingResult {
        /**
         *
         * @return a string representation of the result (which is valid json)
         */
        String toString();

        /**
         *
         * @return the result as JSONObject (for easy manipulating)
         */
        JSONObject toJson();
    }

    void setTargetUrl(final String url);

    void setBufferDuration(final long durationNs);

    void setTargetPlaybackDuration(final long playbackDurationMs);

}
