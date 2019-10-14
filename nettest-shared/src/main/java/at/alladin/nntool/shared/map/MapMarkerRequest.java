package at.alladin.nntool.shared.map;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MapMarkerRequest {

    @Expose
    @SerializedName(value = "coordinates", alternate = "coords")
    @JsonAlias("coords")
    @JsonProperty
    private MapCoordinate coordinates;
    
    @Expose
    @SerializedName("filter")
    @JsonProperty(value = "filter")
    private Map<String, String> mapFilter;
    
    @Expose
    private String language;
    
    @Expose
    private Map<String, String> options;

	public MapCoordinate getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(MapCoordinate coordinate) {
		this.coordinates = coordinate;
	}

	public Map<String, String> getMapFilter() {
		return mapFilter;
	}

	public void setMapFilter(Map<String, String> filter) {
		this.mapFilter = filter;
	}

	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	public Map<String, String> getOptions() {
		return options;
	}

	public void setOptions(Map<String, String> options) {
		this.options = options;
	}
    
}
