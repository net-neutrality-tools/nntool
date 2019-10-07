package at.alladin.nntool.shared.map.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapInfoResponse {
	
	@Expose
	@JsonProperty("map_filters")
	@SerializedName("map_filters")
	private MapFilters mapFilters;
	
	@Expose
	@JsonProperty("map_types")
	@SerializedName("map_types")
	private List<TechnologyTypes> mapTechnologyTypeList;

	public MapFilters getMapFilters() {
		return mapFilters;
	}

	public void setMapFilters(MapFilters mapFilters) {
		this.mapFilters = mapFilters;
	}

	public List<TechnologyTypes> getMapTechnologyTypeList() {
		return mapTechnologyTypeList;
	}

	public void setMapTechnologyTypeList(List<TechnologyTypes> mapTechnologyTypeList) {
		this.mapTechnologyTypeList = mapTechnologyTypeList;
	}

}
