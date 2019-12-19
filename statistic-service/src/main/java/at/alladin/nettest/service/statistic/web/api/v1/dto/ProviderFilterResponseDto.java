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

package at.alladin.nettest.service.statistic.web.api.v1.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.service.statistic.web.api.v1.dto.filter.BasicFilterDto;

/**
 * 
 * @author lb
 *
 */
@io.swagger.annotations.ApiModel(description = "Holds provider statistics filter.")
@JsonClassDescription("Holds provider statistics filter.")
public class ProviderFilterResponseDto {

	@JsonPropertyDescription("Contains a list with pre-configured filter elements including default values.")
	@Expose
	@SerializedName("filters")
	@JsonProperty("filters")
	List<BasicFilterDto<?>> filters = new ArrayList<>();

	public List<BasicFilterDto<?>> getFilters() {
		return filters;
	}

	public void setFilters(List<BasicFilterDto<?>> filters) {
		this.filters = filters;
	}
	
}
