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
 * Contains information about requested number of streams and the actual number of streams used during the speed measurement.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains information about requested number of streams and the actual number of streams used during the speed measurement.")
public class NumStreamsInfo {

	/**
	 * The requested number of streams for the download measurement.
	 */
	@JsonPropertyDescription("The requested number of streams for the download measurement.")
	@Expose
	@SerializedName("requested_num_streams_download")
	@JsonProperty("requested_num_streams_download")
	private Integer requestedNumStreamsDownload;
	
	/**
	 * The requested number of streams for the upload measurement.
	 */
	@JsonPropertyDescription("The requested number of streams for the upload measurement.")
	@Expose
	@SerializedName("requested_num_streams_upload")
	@JsonProperty("requested_num_streams_upload")
	private Integer requestedNumStreamsUpload;
	
	/**
	 * The actual number of streams used by the download measurement.
	 */
	@JsonPropertyDescription("The actual number of streams used by the download measurement.")
	@Expose
	@SerializedName("actual_num_streams_download")
	@JsonProperty("actual_num_streams_download")
	private Integer actualNumStreamsDownload;
	
	/**
	 * The actual number of streams used by the upload measurement.
	 */
	@JsonPropertyDescription("The actual number of streams used by the upload measurement.")
	@Expose
	@SerializedName("actual_num_streams_upload")
	@JsonProperty("actual_num_streams_upload")
	private Integer actualNumStreamsUpload;

	public Integer getRequestedNumStreamsDownload() {
		return requestedNumStreamsDownload;
	}

	public void setRequestedNumStreamsDownload(Integer requestedNumStreamsDownload) {
		this.requestedNumStreamsDownload = requestedNumStreamsDownload;
	}

	public Integer getRequestedNumStreamsUpload() {
		return requestedNumStreamsUpload;
	}

	public void setRequestedNumStreamsUpload(Integer requestedNumStreamsUpload) {
		this.requestedNumStreamsUpload = requestedNumStreamsUpload;
	}

	public Integer getActualNumStreamsDownload() {
		return actualNumStreamsDownload;
	}

	public void setActualNumStreamsDownload(Integer actualNumStreamsDownload) {
		this.actualNumStreamsDownload = actualNumStreamsDownload;
	}

	public Integer getActualNumStreamsUpload() {
		return actualNumStreamsUpload;
	}

	public void setActualNumStreamsUpload(Integer actualNumStreamsUpload) {
		this.actualNumStreamsUpload = actualNumStreamsUpload;
	}
}
