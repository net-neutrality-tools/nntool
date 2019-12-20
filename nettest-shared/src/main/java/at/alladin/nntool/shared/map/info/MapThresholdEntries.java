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

public class MapThresholdEntries {
	
	@Expose
	@SerializedName("threshold_colors")
	@JsonProperty("threshold_colors")
	private List<String> thresholdColors = new ArrayList<>();
	
	@Expose
	@SerializedName("threshold_values")
	@JsonProperty("threshold_values")
	private List<String> thresholdValues = new ArrayList<>();

	public List<String> getThresholdColors() {
		return thresholdColors;
	}

	public void setThresholdColors(List<String> thresholdColors) {
		this.thresholdColors = thresholdColors;
	}

	public List<String> getThresholdValues() {
		return thresholdValues;
	}

	public void setThresholdValues(List<String> thresholdValues) {
		this.thresholdValues = thresholdValues;
	}
	
}
