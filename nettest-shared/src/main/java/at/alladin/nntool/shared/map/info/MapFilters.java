package at.alladin.nntool.shared.map.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MapFilters {

	//possible optimization TODO: make a list out of them => needs adaptation on client sides
	
	@Expose
	@SerializedName("all")
	@JsonProperty("all")
	private List<TechnologyFilter> allTechnolgyFilter = new ArrayList<>();
	
	@Expose
	@SerializedName("browser")
	@JsonProperty("browser")
	private List<TechnologyFilter> browserTechnolgyFilter = new ArrayList<>();
	
	@Expose
	@SerializedName("mobile")
	@JsonProperty("mobile")
	private List<TechnologyFilter> mobileTechnolgyFilter = new ArrayList<>();
	
	@Expose
	@SerializedName("wifi")
	@JsonProperty("wifi")
	private List<TechnologyFilter> wifiTechnologyFilter = new ArrayList<>();

	public List<TechnologyFilter> getAllTechnolgyFilter() {
		return allTechnolgyFilter;
	}

	public void setAllTechnolgyFilter(List<TechnologyFilter> allTechnolgyFilter) {
		this.allTechnolgyFilter = allTechnolgyFilter;
	}

	public List<TechnologyFilter> getBrowserTechnolgyFilter() {
		return browserTechnolgyFilter;
	}

	public void setBrowserTechnolgyFilter(List<TechnologyFilter> browserTechnolgyFilter) {
		this.browserTechnolgyFilter = browserTechnolgyFilter;
	}

	public List<TechnologyFilter> getMobileTechnolgyFilter() {
		return mobileTechnolgyFilter;
	}

	public void setMobileTechnolgyFilter(List<TechnologyFilter> mobileTechnolgyFilter) {
		this.mobileTechnolgyFilter = mobileTechnolgyFilter;
	}

	public List<TechnologyFilter> getWifiTechnologyFilter() {
		return wifiTechnologyFilter;
	}

	public void setWifiTechnologyFilter(List<TechnologyFilter> wifiTechnologyFilter) {
		this.wifiTechnologyFilter = wifiTechnologyFilter;
	}

	
}
