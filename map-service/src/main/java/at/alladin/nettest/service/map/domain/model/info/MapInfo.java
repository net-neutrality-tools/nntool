package at.alladin.nettest.service.map.domain.model.info;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import at.alladin.nettest.service.map.domain.model.MapTypes;

public class MapInfo {
	
	@Expose
	private MapFilters mapFilters;
	
	@Expose
	@JsonProperty("mapTypes")
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
