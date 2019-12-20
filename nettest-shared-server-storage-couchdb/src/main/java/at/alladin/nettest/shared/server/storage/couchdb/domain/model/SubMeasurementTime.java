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
 * This class extends the MeasurementTime class with relative time stamps.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("This class extends the MeasurementTime class with relative time stamps.")
public class SubMeasurementTime extends MeasurementTime {

	/**
	 * Start time in nanoseconds relative to the start time of the overall measurement object.
	 */
	@JsonPropertyDescription("Start time in nanoseconds relative to the start time of the overall measurement object.")
	@Expose
	@SerializedName("relative_start_time_ns")
	@JsonProperty("relative_start_time_ns")
	private Long relativeStartTimeNs;
	
	/**
	 * End time in nanoseconds relative to the end time of the overall measurement object.
	 */
	@JsonPropertyDescription("End time in nanoseconds relative to the end time of the overall measurement object.")
	@Expose
	@SerializedName("relative_end_time_ns")
	@JsonProperty("relative_end_time_ns")
	private Long relativeEndTimeNs;

	public Long getRelativeStartTimeNs() {
		return relativeStartTimeNs;
	}

	public void setRelativeStartTimeNs(Long relativeStartTimeNs) {
		this.relativeStartTimeNs = relativeStartTimeNs;
	}

	public Long getRelativeEndTimeNs() {
		return relativeEndTimeNs;
	}

	public void setRelativeEndTimeNs(Long relativeEndTimeNs) {
		this.relativeEndTimeNs = relativeEndTimeNs;
	}
}