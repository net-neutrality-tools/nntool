package at.alladin.nettest.service.map.domain.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.shared.map.info.TechnologyTypes;

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
