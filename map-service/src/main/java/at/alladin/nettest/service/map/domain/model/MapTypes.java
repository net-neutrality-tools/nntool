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

package at.alladin.nettest.service.map.domain.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import at.alladin.nntool.shared.map.info.TechnologyTypes;

public class MapTypes {
	
	@Expose
	private List<TechnologyTypes> technologyTypeList = new ArrayList<>();

	public List<TechnologyTypes> getTechnolgyTypeList() {
		return technologyTypeList;
	}

	public void setTechnolgyTypeList(List<TechnologyTypes> technolgyTypeList) {
		this.technologyTypeList = technolgyTypeList;
	}
	
	
	
}
