package at.alladin.nettest.service.map.domain.model.info;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TechnologyTypes {
	
	public TechnologyTypes () {
		
	}

	@Expose
	private String title;

	@Expose
	@SerializedName("options")
	@JsonProperty("options")
	private List<MapAppearanceInfo> appearanceInfoList = new ArrayList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<MapAppearanceInfo> getAppearanceInfoList() {
		return appearanceInfoList;
	}

	public void setAppearanceInfoList(List<MapAppearanceInfo> appearanceInfoList) {
		this.appearanceInfoList = appearanceInfoList;
	}
	
}
