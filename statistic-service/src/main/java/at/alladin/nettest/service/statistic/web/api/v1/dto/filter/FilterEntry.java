package at.alladin.nettest.service.statistic.web.api.v1.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author lb
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=Include.NON_EMPTY)
public class FilterEntry<T> {

	@JsonPropertyDescription("A label for this filter element.")
	@Expose
	@SerializedName("label")
	@JsonProperty("label")
	String label;
	
	@JsonPropertyDescription("The value of this filter element.")
	@Expose
	@SerializedName("value")
	@JsonProperty("value")
	T value;
	
	@JsonPropertyDescription("The min value of this filter element.")
	@Expose
	@SerializedName("min")
	@JsonProperty("min")
	T min;
	
	@JsonPropertyDescription("The max value of this filter element.")
	@Expose
	@SerializedName("max")
	@JsonProperty("max")
	T max;

	public FilterEntry() { }
	
	public FilterEntry(final String label, final T value) {
		this(label, value, null, null);
	}

	public FilterEntry(final String label, final T value, T min, T max) {
		this.label = label;
		this.value = value;
		this.min = min;
		this.max = max;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getMin() {
		return min;
	}

	public void setMin(T min) {
		this.min = min;
	}

	public T getMax() {
		return max;
	}

	public void setMax(T max) {
		this.max = max;
	}
}
