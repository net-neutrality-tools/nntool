/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Contains information about captured byte transfers during the speed measurement from a point of time.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about captured byte transfers during the speed measurement from a point of time.")
public class SpeedMeasurementRawDataItem {
	
	/**
	 * The stream id (numeric value starting from 0).
	 */
	@JsonPropertyDescription("The stream id (numeric value starting from 0).")
	@Expose
	@SerializedName("stream_id")
	@JsonProperty("stream_id")
    private Integer streamId;
    
    /**
     * Relative time since start of the speed measurement.
     */
	@JsonPropertyDescription("Relative time since start of the speed measurement.")
    @Expose
    @SerializedName("relative_time_ns")
    @JsonProperty("relative_time_ns")
    private Long relativeTimeNs;
    
    /**
     * Bytes transmitted or received since start of the speed measurement.
     */
	@JsonPropertyDescription("Bytes transmitted or received since start of the speed measurement.")
    @Expose
    @SerializedName("bytes")
    @JsonProperty("bytes")
    private Long bytes;

	/**
	 * Bytes transmitted or received since start of the speed measurement, including the slow start phase.
	 */
	@JsonPropertyDescription("Bytes transmitted or received since start of the speed measurement, including the slow start phase.")
	@Expose
	@SerializedName("bytes_including_slow_start")
	@JsonProperty("bytes_including_slow_start")
	private Long bytesIncludingSlowStart;

	public Integer getStreamId() {
		return streamId;
	}

	public void setStreamId(Integer streamId) {
		this.streamId = streamId;
	}

	public Long getRelativeTimeNs() {
		return relativeTimeNs;
	}

	public void setRelativeTimeNs(Long relativeTimeNs) {
		this.relativeTimeNs = relativeTimeNs;
	}

	public Long getBytes() {
		return bytes;
	}

	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}

	public Long getBytesIncludingSlowStart() {
		return bytesIncludingSlowStart;
	}

	public void setBytesIncludingSlowStart(Long bytesIncludingSlowStart) {
		this.bytesIncludingSlowStart = bytesIncludingSlowStart;
	}
}