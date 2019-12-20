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
