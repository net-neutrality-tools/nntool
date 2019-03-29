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
