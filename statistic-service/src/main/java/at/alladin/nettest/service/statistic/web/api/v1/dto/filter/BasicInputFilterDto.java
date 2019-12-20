/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
