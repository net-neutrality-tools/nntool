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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		property = "deserialize_type"	//TODO: rename all deserialize_type to _deserialize_type
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = DeviceOption.class, name = "device_option"),
		@JsonSubTypes.Type(value = OperatorOption.class, name = "operator_option"),
		@JsonSubTypes.Type(value = ProviderOption.class, name = "provider_option"),
		@JsonSubTypes.Type(value = StatisticalOption.class, name = "statistical_option"),
		@JsonSubTypes.Type(value = TechnologyOption.class, name = "technology_option"),
		@JsonSubTypes.Type(value = TimePeriodOption.class, name = "time_period_option")
})
public abstract class AbstractOption {

	@Expose
	protected String title;
	
	@Expose
	protected String summary;
	
	@Expose
	@SerializedName("default")
	@JsonProperty("default")
	@JsonInclude(Include.NON_NULL)
	protected Boolean isDefault;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	
}
