package at.alladin.nettest.service.map.domain.model.info;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.service.map.domain.model.info.option.AbstractOption;

public class TechnologyFilter {
	
	public TechnologyFilter() {
		
	}
	
	public TechnologyFilter(final String title, final String key) {
		this.title = title;
		this.key = key;
	}

	@Expose
	private String title;
	
	@Expose
	@SerializedName("_key")
	@JsonProperty("_key")
	private String key;
	
	@Expose
	private List<AbstractOption> options = new ArrayList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<AbstractOption> getOptions() {
		return options;
	}

	public void setOptions(List<AbstractOption> options) {
		this.options = options;
	}
	
}
