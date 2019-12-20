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

package at.alladin.nntool.shared.map.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapInfoResponse {
	
	@Expose
	@JsonProperty("map_filters")
	@SerializedName("map_filters")
	private MapFilters mapFilters;
	
	@Expose
	@JsonProperty("map_types")
	@SerializedName("map_types")
	private List<TechnologyTypes> mapTechnologyTypeList;

	public MapFilters getMapFilters() {
		return mapFilters;
	}

	public void setMapFilters(MapFilters mapFilters) {
		this.mapFilters = mapFilters;
	}

	public List<TechnologyTypes> getMapTechnologyTypeList() {
		return mapTechnologyTypeList;
	}

	public void setMapTechnologyTypeList(List<TechnologyTypes> mapTechnologyTypeList) {
		this.mapTechnologyTypeList = mapTechnologyTypeList;
	}

}
