package at.alladin.nettest.service.map.domain.model.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapAppearanceInfo {
	
	public MapAppearanceInfo () {
	
	}
	
	public MapAppearanceInfo (final String title, final String unit, final String mapOptions, final String overlayType, final String summary) {
		this.title = title;
		this.unit = unit;
		this.mapOptions = mapOptions;
		this.overlayType = overlayType;
		this.summary = summary;
	}

	@Expose
	private String title;
	
	@Expose
	private String unit;
	
	@Expose
	@SerializedName("map_options")
	@JsonProperty("map_options")
	private String mapOptions;
	
	@Expose
	@SerializedName("overlay_type")
	@JsonProperty("overlay_type")
	private String overlayType;
	
	@Expose
	private String summary;
	
	@Expose
	private Map<String, MapThresholdEntries> thresholds = new HashMap<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMapOptions() {
		return mapOptions;
	}

	public void setMapOptions(String mapOptions) {
		this.mapOptions = mapOptions;
	}

	public String getOverlayType() {
		return overlayType;
	}

	public void setOverlayType(String overlayType) {
		this.overlayType = overlayType;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Map<String, MapThresholdEntries> getThresholds() {
		return thresholds;
	}

	public void setThresholds(Map<String, MapThresholdEntries> thresholds) {
		this.thresholds = thresholds;
	}
	
}
