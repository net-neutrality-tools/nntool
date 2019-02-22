package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class contains additional information gathered from the WebSocket protocol.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("This class contains additional information gathered from the WebSocket protocol.")
public class WebSocketInfo {

	/**
	 * Size of a transmitted frame over the WebSocket protocol.
	 */
	@JsonPropertyDescription("Size of a transmitted frame over the WebSocket protocol.")
	@Expose
	@SerializedName("frame_size")
	@JsonProperty("frame_size")
	private Integer frameSize; // *load_frame_size
	
	/**
	 * Number of frames sent over the WebSocket protocol during measurement excluding slow-start phase.
	 */
	@JsonPropertyDescription("Number of frames sent over the WebSocket protocol during measurement excluding slow-start phase.")
	@Expose
	@SerializedName("frame_count")
	@JsonProperty("frame_count")
	private Integer frameCount; // *load_frames
	
	/**
	 * Number of frames sent over the WebSocket protocol during measurement including slow-start phase.
	 */
	@JsonPropertyDescription("Number of frames sent over the WebSocket protocol during measurement including slow-start phase.")
	@Expose
	@SerializedName("frame_count_including_slow_start")
	@JsonProperty("frame_count_including_slow_start")
	private Integer frameCountIncludingSlowStart; // *load_frames_total
	
	/**
	 * The overhead sent during the communication via the WebSocket protocol excluding slow-start phase.
	 */
	@JsonPropertyDescription("The overhead sent during the communication via the WebSocket protocol excluding slow-start phase.")
	@Expose
	@SerializedName("overhead")
	@JsonProperty("overhead")
	private Integer overhead; // *load_overhead
	
	/**
	 * The overhead sent during the communication via the WebSocket protocol including slow-start phase.
	 */
	@JsonPropertyDescription("The overhead sent during the communication via the WebSocket protocol including slow-start phase.")
	@Expose
	@SerializedName("overhead_per_frame_including_slow_start")
	@JsonProperty("overhead_per_frame_including_slow_start")
	private Integer overheadPerFrameIncludingSlowStart; // *load_overhead_total
	
	/**
	 * The overhead a single frame produces on average.
	 */
	@JsonPropertyDescription("The overhead a single frame produces on average.")
	@Expose
	@SerializedName("overhead_per_frame")
	@JsonProperty("overhead_per_frame")
	private Integer overheadPerFrame; // *load_overhead_per_frame

	public Integer getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(Integer frameSize) {
		this.frameSize = frameSize;
	}

	public Integer getFrameCount() {
		return frameCount;
	}

	public void setFrameCount(Integer frameCount) {
		this.frameCount = frameCount;
	}

	public Integer getFrameCountIncludingSlowStart() {
		return frameCountIncludingSlowStart;
	}

	public void setFrameCountIncludingSlowStart(Integer frameCountIncludingSlowStart) {
		this.frameCountIncludingSlowStart = frameCountIncludingSlowStart;
	}

	public Integer getOverhead() {
		return overhead;
	}

	public void setOverhead(Integer overhead) {
		this.overhead = overhead;
	}

	public Integer getOverheadPerFrameIncludingSlowStart() {
		return overheadPerFrameIncludingSlowStart;
	}

	public void setOverheadPerFrameIncludingSlowStart(Integer overheadPerFrameIncludingSlowStart) {
		this.overheadPerFrameIncludingSlowStart = overheadPerFrameIncludingSlowStart;
	}

	public Integer getOverheadPerFrame() {
		return overheadPerFrame;
	}

	public void setOverheadPerFrame(Integer overheadPerFrame) {
		this.overheadPerFrame = overheadPerFrame;
	}
}
