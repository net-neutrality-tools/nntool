package at.alladin.nntool.shared.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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

		/**
		 * Contains a translated list of results to be displayed as is from the clients
		 * (Only some have the classifications set)
		 */
		@Expose
		@SerializedName("result_items")
		@JsonProperty("result_items")
		private List<MarkerItem> resultItems;

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
		@SerializedName("time_stamp")
		@JsonProperty("time_stamp")
		private Long timestamp;

		@Expose
		@SerializedName("net")
		@JsonProperty("net")
		private List<MarkerItem> networkResult;

		@Expose
		@SerializedName("measurement")
		@JsonProperty("measurement")
		private List<MarkerItem> measurementResults;

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

		public Long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Long timestamp) {
			this.timestamp = timestamp;
		}

		public List<MarkerItem> getNetworkResult() {
			return networkResult;
		}

		public void setNetworkResult(List<MarkerItem> networkResult) {
			this.networkResult = networkResult;
		}

		public List<MarkerItem> getMeasurementResults() {
			return measurementResults;
		}

		public void setMeasurementResults(List<MarkerItem> measurementResults) {
			this.measurementResults = measurementResults;
		}

		public List<MarkerItem> getResultItems() {
			return resultItems;
		}

		public void setResultItems(List<MarkerItem> resultItems) {
			this.resultItems = resultItems;
		}
	}

	public static class MarkerItem {

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
