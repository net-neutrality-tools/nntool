package at.alladin.nettest.service.map.domain.model.info;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MapThresholdEntries {
	
	@Expose
	@SerializedName("threshold_colors")
	@JsonProperty("threshold_colors")
	private List<String> thresholdColors = new ArrayList<>();
	
	@Expose
	@SerializedName("threshold_values")
	@JsonProperty("threshold_values")
	private List<String> thresholdValues = new ArrayList<>();

	public List<String> getThresholdColors() {
		return thresholdColors;
	}

	public void setThresholdColors(List<String> thresholdColors) {
		this.thresholdColors = thresholdColors;
	}

	public List<String> getThresholdValues() {
		return thresholdValues;
	}

	public void setThresholdValues(List<String> thresholdValues) {
		this.thresholdValues = thresholdValues;
	}
	
}
