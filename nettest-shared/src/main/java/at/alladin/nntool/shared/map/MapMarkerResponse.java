package at.alladin.nntool.shared.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementGroupItem;

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
		private List<DetailMeasurementGroupItem> resultItems;

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
		private List<DetailMeasurementGroupItem> networkResult;

		@Expose
		@SerializedName("measurement")
		@JsonProperty("measurement")
		private List<DetailMeasurementGroupItem> measurementResults;

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

		public List<DetailMeasurementGroupItem> getNetworkResult() {
			return networkResult;
		}

		public void setNetworkResult(List<DetailMeasurementGroupItem> networkResult) {
			this.networkResult = networkResult;
		}

		public List<DetailMeasurementGroupItem> getMeasurementResults() {
			return measurementResults;
		}

		public void setMeasurementResults(List<DetailMeasurementGroupItem> measurementResults) {
			this.measurementResults = measurementResults;
		}

		public List<DetailMeasurementGroupItem> getResultItems() {
			return resultItems;
		}

		public void setResultItems(List<DetailMeasurementGroupItem> resultItems) {
			this.resultItems = resultItems;
		}
	}

}
