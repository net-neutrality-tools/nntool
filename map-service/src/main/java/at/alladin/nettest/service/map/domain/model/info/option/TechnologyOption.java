package at.alladin.nettest.service.map.domain.model.info.option;

import com.google.gson.annotations.Expose;

public class TechnologyOption extends AbstractOption {
	
	public TechnologyOption() {
		
	}
	
	public TechnologyOption(final String title, final String summary, final String technology) {
		this(title, summary, technology, null);
	}
	
	public TechnologyOption(final String title, final String summary, final String technology, final Boolean isDefault) {
		this.technology = technology;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}

	@Expose
	private String technology;

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}
	
}
