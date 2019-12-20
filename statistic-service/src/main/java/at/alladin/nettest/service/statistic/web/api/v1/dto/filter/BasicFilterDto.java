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

import com.fasterxml.jackson.annotation.JsonClassDescription;
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
@io.swagger.annotations.ApiModel(description = "Basic Filter DTO.")
@JsonClassDescription("Basic Filter DTO.")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(value=Include.NON_EMPTY)
public class BasicFilterDto<T> {

	@JsonPropertyDescription("The key (id) of this filter.")
	@Expose
	@SerializedName("key")
	@JsonProperty("key")
	private String key;
	
	@JsonPropertyDescription("The type of this filter (= filter element, e.g. dropdown, input field)")
	@Expose
	@SerializedName("filter_type")
	@JsonProperty("filter_type")
	private FilterTypeDto filterType;
	
	@JsonPropertyDescription("Contains the default (= selected) value of this filter.")
	@Expose
	@SerializedName("default_value")
	@JsonProperty("default_value")
	private T defaultValue;
	
	public FilterTypeDto getFilterType() {
		return filterType;
	}

	public void setFilterType(FilterTypeDto filterType) {
		this.filterType = filterType;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
