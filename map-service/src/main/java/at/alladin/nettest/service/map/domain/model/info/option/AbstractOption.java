package at.alladin.nettest.service.map.domain.model.info.option;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public abstract class AbstractOption {

	@Expose
	protected String title;
	
	@Expose
	protected String summary;
	
	@Expose
	@SerializedName("default")
	@JsonProperty("default")
	@JsonInclude(Include.NON_NULL)
	protected Boolean isDefault;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	
}
