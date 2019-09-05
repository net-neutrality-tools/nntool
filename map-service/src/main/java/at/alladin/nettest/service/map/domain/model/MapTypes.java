package at.alladin.nettest.service.map.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import at.alladin.nettest.service.map.domain.model.info.TechnologyTypes;

public class MapTypes {
	
	@Expose
	private List<TechnologyTypes> technologyTypeList = new ArrayList<>();

	public List<TechnologyTypes> getTechnolgyTypeList() {
		return technologyTypeList;
	}

	public void setTechnolgyTypeList(List<TechnologyTypes> technolgyTypeList) {
		this.technologyTypeList = technolgyTypeList;
	}
	
	
	
}
