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

import java.util.ArrayList;
import java.util.List;

public class TechnologyTypes {
	
	public TechnologyTypes () {
		
	}

	@Expose
	private String title;

	@Expose
	@SerializedName("options")
	@JsonProperty("options")
	private List<MapAppearanceInfo> appearanceInfoList = new ArrayList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<MapAppearanceInfo> getAppearanceInfoList() {
		return appearanceInfoList;
	}

	public void setAppearanceInfoList(List<MapAppearanceInfo> appearanceInfoList) {
		this.appearanceInfoList = appearanceInfoList;
	}
	
}
