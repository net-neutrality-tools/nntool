/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb
 *
 */
public class HistoryItem { // rename BriefMeasurementResponse

	@SerializedName("test_uuid")
	@Expose
	String testUuid;

	@Expose
	Long time;
	
	@SerializedName("time_zone")
	@Expose
	String timeZone;

	@SerializedName("time_string")
	@Expose
	String timeString;
	
	@SerializedName("speed_upload")
	@Expose
	String speedUpload;

	@SerializedName("speed_download")
	@Expose
	String speedDownload;
	
	@Expose
	String ping;
	
	@SerializedName("ping_shortest")
	@Expose
	String pingShortest;
	
	@Expose
	String model;
	
	@SerializedName("network_type")
	@Expose
	String networkType;
	
	@SerializedName("speed_upload_classification")
	@Expose
	Integer speedUploadClassification;
	
	@SerializedName("speed_download_classification")
	@Expose
	Integer speedDownloadClassification;
	
	@SerializedName("ping_classification")
	@Expose
	Integer pingClassification;
	
	@SerializedName("ping_short_classification")
	@Expose
	Integer pingShortClassification;
	
	@SerializedName("speed_upload_classification_color")
	@Expose
	String speedUploadClassificationColor;
	
	@SerializedName("speed_download_classification_color")
	@Expose
	String speedDownloadClassificationColor;
	
	@SerializedName("ping_classification_color")
	@Expose
	String pingClassificationColor;
	
	@SerializedName("ping_short_classification_color")
	@Expose
	String pingShortClassificationColor;
	
	@SerializedName("qos_result_available")
	@Expose
	private Boolean qosResultAvailable;

	public String getTestUuid() {
		return testUuid;
	}

	public void setTestUuid(String testUuid) {
		this.testUuid = testUuid;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getSpeedUpload() {
		return speedUpload;
	}

	public void setSpeedUpload(String speedUpload) {
		this.speedUpload = speedUpload;
	}

	public String getSpeedDownload() {
		return speedDownload;
	}

	public void setSpeedDownload(String speedDownload) {
		this.speedDownload = speedDownload;
	}

	public String getPing() {
		return ping;
	}

	public void setPing(String ping) {
		this.ping = ping;
	}

	public String getPingShortest() {
		return pingShortest;
	}

	public void setPingShortest(String pingShortest) {
		this.pingShortest = pingShortest;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public Integer getSpeedUploadClassification() {
		return speedUploadClassification;
	}

	public void setSpeedUploadClassification(Integer speedUploadClassification) {
		this.speedUploadClassification = speedUploadClassification;
	}

	public Integer getSpeedDownloadClassification() {
		return speedDownloadClassification;
	}

	public void setSpeedDownloadClassification(Integer speedDownloadClassification) {
		this.speedDownloadClassification = speedDownloadClassification;
	}

	public Integer getPingClassification() {
		return pingClassification;
	}

	public void setPingClassification(Integer pingClassification) {
		this.pingClassification = pingClassification;
	}

	public Integer getPingShortClassification() {
		return pingShortClassification;
	}

	public void setPingShortClassification(Integer pingShortClassification) {
		this.pingShortClassification = pingShortClassification;
	}
	
	public Boolean isQosResultAvailable() {
		return qosResultAvailable;
	}
	
	public void setQosResultAvailable(Boolean qosResultAvailable) {
		this.qosResultAvailable = qosResultAvailable;
	}

	public String getSpeedUploadClassificationColor() {
		return speedUploadClassificationColor;
	}

	public void setSpeedUploadClassificationColor(String speedUploadClassificationColor) {
		this.speedUploadClassificationColor = speedUploadClassificationColor;
	}

	public String getSpeedDownloadClassificationColor() {
		return speedDownloadClassificationColor;
	}

	public void setSpeedDownloadClassificationColor(String speedDownloadClassificationColor) {
		this.speedDownloadClassificationColor = speedDownloadClassificationColor;
	}

	public String getPingClassificationColor() {
		return pingClassificationColor;
	}

	public void setPingClassificationColor(String pingClassificationColor) {
		this.pingClassificationColor = pingClassificationColor;
	}

	public String getPingShortClassificationColor() {
		return pingShortClassificationColor;
	}

	public void setPingShortClassificationColor(String pingShortClassificationColor) {
		this.pingShortClassificationColor = pingShortClassificationColor;
	}
	
}