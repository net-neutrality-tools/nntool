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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Stores the raw data (amount of bytes in time) values for download and upload.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Stores the raw data (amount of bytes in time) values for download and upload.")
public class SpeedMeasurementRawData {

	/**
	 * Contains a list of all captured byte transfers during the download speed measurement.
	 */
	@JsonPropertyDescription("Contains a list of all captured byte transfers during the download speed measurement.")
	@Expose
	@SerializedName("download")
	@JsonProperty("download")
	private List<SpeedMeasurementRawDataItem> download;
	
	/**
	 * Contains a list of all captured byte transfers during the upload speed measurement.
	 */
	@JsonPropertyDescription("Contains a list of all captured byte transfers during the upload speed measurement.")
	@Expose
	@SerializedName("upload")
	@JsonProperty("upload")
	private List<SpeedMeasurementRawDataItem> upload;
	
	public List<SpeedMeasurementRawDataItem> getDownload() {
		return download;
	}
	
	public void setDownload(List<SpeedMeasurementRawDataItem> download) {
		this.download = download;
	}
	
	public List<SpeedMeasurementRawDataItem> getUpload() {
		return upload;
	}
	
	public void setUpload(List<SpeedMeasurementRawDataItem> upload) {
		this.upload = upload;
	}
}
