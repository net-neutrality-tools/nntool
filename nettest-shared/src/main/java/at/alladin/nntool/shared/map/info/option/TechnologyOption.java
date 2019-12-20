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

import com.google.gson.annotations.Expose;

public class TechnologyOption extends AbstractOption {
	
	public TechnologyOption() {
		
	}
	
	public TechnologyOption(final String title, final String summary, final String technology) {
		this(title, summary, technology, null);
	}
	
	public TechnologyOption(final String title, final String summary, final String technology, final Boolean isDefault) {
		this.technology = technology;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}

	@Expose
	private String technology;

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}
	
}
