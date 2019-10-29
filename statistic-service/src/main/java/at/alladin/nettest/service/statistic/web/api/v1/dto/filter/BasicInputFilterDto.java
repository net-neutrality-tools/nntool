package at.alladin.nettest.service.statistic.web.api.v1.dto.filter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BasicInputFilterDto<T> extends BasicFilterDto<T> {
	
	public static enum InputFilterType {
		NUMBER,
		TEXT
	}

	@JsonPropertyDescription("Contains necessary information about this filter type.")
	@Expose
	@SerializedName("option")
	@JsonProperty("option")
	FilterEntry<T> option;
	
	public BasicInputFilterDto() {
		this(null, null, InputFilterType.TEXT);
	}
	
	public BasicInputFilterDto(final String key, final T defaultValue, final InputFilterType type) {
		switch (type) {
		case NUMBER:
			setFilterType(FilterTypeDto.INPUT_NUMBER);
			break;
		case TEXT:
			setFilterType(FilterTypeDto.INPUT_TEXT);
			break;
		}
		setDefaultValue(defaultValue);
		setKey(key);
	}

	public FilterEntry<T> getOption() {
		return option;
	}

	public void setOption(FilterEntry<T> option) {
		this.option = option;
	}
}
