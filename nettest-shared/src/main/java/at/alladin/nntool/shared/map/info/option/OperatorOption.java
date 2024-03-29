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

public class OperatorOption extends AbstractOption {
	
	public OperatorOption() {
		
	}
	
	public OperatorOption(final String title, final String summary, final String operator) {
		this(title, summary, operator, null);
	}
	
	public OperatorOption(final String title, final String summary, final String operator, final Boolean isDefault) {
		this.operator = operator;
		this.title = title;
		this.summary = summary;
		this.isDefault = isDefault;
	}
	
	@Expose
	private String operator;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
