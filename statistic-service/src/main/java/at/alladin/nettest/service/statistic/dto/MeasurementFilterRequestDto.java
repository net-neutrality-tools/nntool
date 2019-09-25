package at.alladin.nettest.service.statistic.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb
 *
 */
public class MeasurementFilterRequestDto {
	
	@JsonPropertyDescription("Contains the selected filters as a key/value map.")
	@Expose
	@SerializedName("selected_filters")
	@JsonProperty("selected_filters")
	Map<String, Object> selectedFilters = new HashMap<>();

	public Map<String, Object> getSelectedFilters() {
		return selectedFilters;
	}

	public void setSelectedFilters(Map<String, Object> selectedFilters) {
		this.selectedFilters = selectedFilters;
	}
	
}
