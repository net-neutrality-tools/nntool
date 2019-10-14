package at.alladin.nntool.shared.map.info.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatisticalOption extends AbstractOption {
	
	public StatisticalOption() {
		
	}
	
	public StatisticalOption(final String title, final String summary, final Double statisticalMethod) {
		this(title, summary, statisticalMethod, null);
	}
	
	public StatisticalOption(final String title, final String summary, final Double statisticalMethod, final Boolean isDefault) {
		this.statisticalMethod = statisticalMethod;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}
	
	@Expose
	@SerializedName("statistical_method")
	@JsonProperty("statistical_method")
	private Double statisticalMethod;

	public Double getStatisticalMethod() {
		return statisticalMethod;
	}

	public void setStatisticalMethod(Double statisticalMethod) {
		this.statisticalMethod = statisticalMethod;
	}
	
}
