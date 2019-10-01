package at.alladin.nntool.shared.map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapMarkerResponse {

	@Expose
	@SerializedName("measurements")
	@JsonProperty("measurements")
	private List<MapMarker> mapMarkers;

	public List<MapMarker> getMapMarkers() {
		return mapMarkers;
	}

	public void setMapMarkers(List<MapMarker> mapMarkers) {
		this.mapMarkers = mapMarkers;
	}

	@Override
	public String toString() {
		return "MapMarkerResponse{" +
				"mapMarkers=" + mapMarkers +
				'}';
	}

	public static class MapMarker {

		@Expose
		private Boolean highlight;

		@Expose
		@SerializedName("open_test_uuid")
		@JsonProperty("open_test_uuid")
		private String openTestUuid;

		@Expose
		@SerializedName("geo_lat")
		@JsonProperty("geo_lat")
		private Double latitude;

		@Expose
		@SerializedName("geo_long")
		@JsonProperty("geo_long")
		private Double longitude;

		@Expose
		private String time;

		@Expose
		@SerializedName("time_string")
		@JsonProperty("time_string")
		private String timeString;

		@Expose
		private Long timestamp;

		@Expose
		@SerializedName("net")
		@JsonProperty("net")
		private List<MeasurementItem> networkResult;

		@Expose
		@SerializedName("measurement")
		@JsonProperty("measurement")
		private List<MeasurementItem> measurementResults;

		public Boolean getHighlight() {
			return highlight;
		}

		public void setHighlight(Boolean highlight) {
			this.highlight = highlight;
		}

		public String getOpenTestUuid() {
			return openTestUuid;
		}

		public void setOpenTestUuid(String openTestUuid) {
			this.openTestUuid = openTestUuid;
		}

		public Double getLatitude() {
			return latitude;
		}

		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public Double getLongitude() {
			return longitude;
		}

		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getTimeString() {
			return timeString;
		}

		public void setTimeString(String timeString) {
			this.timeString = timeString;
		}

		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public List<MeasurementItem> getMeasurementResults() {
			return measurementResults;
		}

		public void setMeasurementResults(List<MeasurementItem> measurementResults) {
			this.measurementResults = measurementResults;
		}

		public List<MeasurementItem> getNetworkResult() {
			return networkResult;
		}

		public void setNetworkResult(List<MeasurementItem> networkResult) {
			this.networkResult = networkResult;
		}

		@Override
		public String toString() {
			return "MapMarker{" +
					"highlight=" + highlight +
					", openTestUuid='" + openTestUuid + '\'' +
					", latitude=" + latitude +
					", longitude=" + longitude +
					", time='" + time + '\'' +
					", timeString='" + timeString + '\'' +
					", timestamp=" + timestamp +
					", networkResult=" + networkResult +
					", measurementResults=" + measurementResults +
					'}';
		}
	}

	public static class MeasurementItem {

		@Expose
		private String title;

		@Expose
		private String value;

		@Expose
		private Integer classification;

		@Expose
		@SerializedName("classification_color")
		@JsonProperty("classification_color")
		private String classificationColor;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Integer getClassification() {
			return classification;
		}

		public void setClassification(Integer classification) {
			this.classification = classification;
		}

		public String getClassificationColor() {
			return classificationColor;
		}

		public void setClassificationColor(String classificationColor) {
			this.classificationColor = classificationColor;
		}

		@Override
		public String toString() {
			return "MeasurementResult{" +
					"title='" + title + '\'' +
					", value='" + value + '\'' +
					", classification=" + classification +
					", classificationColor='" + classificationColor + '\'' +
					'}';
		}
	}
    
}
