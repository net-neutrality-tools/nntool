package at.alladin.nettest.service.statistic.dto.filter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicDropdownFilterDto<T> extends BasicFilterDto<T> {

	@JsonPropertyDescription("Contains all available options for this dropdown filter.")
	@Expose
	@SerializedName("options")
	@JsonProperty("options")
	List<FilterEntry<T>> options = new ArrayList<>();
	
	public BasicDropdownFilterDto() {
		this(null, null);
	}
	
	public BasicDropdownFilterDto(final String key, final T defaultValue) {
		setFilterType(FilterTypeDto.DROPDOWN);
		setDefaultValue(defaultValue);
		setKey(key);
	}

	public List<FilterEntry<T>> getOptions() {
		return options;
	}

	public void setOptions(List<FilterEntry<T>> options) {
		this.options = options;
	}
}
