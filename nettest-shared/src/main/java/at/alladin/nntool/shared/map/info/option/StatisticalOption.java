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

package at.alladin.nntool.shared.map.info.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatisticalOption extends AbstractOption {
	
	public StatisticalOption() {
		
	}
	
	public StatisticalOption(final String title, final String summary, final Double statisticalMethod) {
		this(title, summary, statisticalMethod, null);
	}
	
	public StatisticalOption(final String title, final String summary, final Double statisticalMethod, final Boolean isDefault) {
		this.statisticalMethod = statisticalMethod;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}
	
	@Expose
	@SerializedName("statistical_method")
	@JsonProperty("statistical_method")
	private Double statisticalMethod;

	public Double getStatisticalMethod() {
		return statisticalMethod;
	}

	public void setStatisticalMethod(Double statisticalMethod) {
		this.statisticalMethod = statisticalMethod;
	}
	
}
