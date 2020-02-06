package at.alladin.nntool.shared.qos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Felix Kendlbacher (alladin-IT GmbH)
 */
public class AudioStreamingResult extends AbstractResult {
	
	/**
	 * The time it took from start playing command to the actual start of the audio stream
	 */
	@JsonProperty("audio_start_time_ns")
	private Object audioStartTime;
	
	@JsonProperty("stalls_ns")
	private List<Long> stalls;
	
	@JsonProperty("average_stall_time")
	private Object averageStallTime;
	
	@JsonProperty("number_of_stalls")
	private Object numberOfStalls;
	
	@JsonProperty("total_stall_time")
	private Object totalStallTime;
	
	@JsonProperty("audio_streaming_objective_target_url")
	private String targetUrl;
	
	@JsonProperty("audio_streaming_objective_playback_duration_ms")
	private Object playbackDurationMs;

	public Object getAudioStartTime() {
		return audioStartTime;
	}

	public void setAudioStartTime(Object audioStartTime) {
		this.audioStartTime = audioStartTime;
	}

	public List<Long> getStalls() {
		return stalls;
	}

	public void setStalls(List<Long> stalls) {
		this.stalls = stalls;
	}

	public Object getAverageStallTime() {
		return averageStallTime;
	}

	public void setAverageStallTime(Object averageStallTime) {
		this.averageStallTime = averageStallTime;
	}

	public Object getNumberOfStalls() {
		return numberOfStalls;
	}

	public void setNumberOfStalls(Object numberOfStalls) {
		this.numberOfStalls = numberOfStalls;
	}

	public Object getTotalStallTime() {
		return totalStallTime;
	}

	public void setTotalStallTime(Object totalStallTime) {
		this.totalStallTime = totalStallTime;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public Object getPlaybackDurationMs() {
		return playbackDurationMs;
	}

	public void setPlaybackDurationMs(Object playbackDurationMs) {
		this.playbackDurationMs = playbackDurationMs;
	}

}
