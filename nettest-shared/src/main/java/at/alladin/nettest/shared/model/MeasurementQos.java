/*******************************************************************************
 * Copyright 2017-2019 alladin-IT GmbH
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

package at.alladin.nettest.shared.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * @author lb
 */
public class MeasurementQos {

	/**
	 * 
	 */
	@Expose
	private String status; // TODO: enum
	
	/**
	 * 
	 */
	@Expose
	private List<MeasurementQosResult> results = new ArrayList<>();
	
	/**
	 * 
	 */
	public MeasurementQos() {
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<MeasurementQosResult> getResults() {
		return results;
	}
	
	/**
	 * 
	 * @param results
	 */
	public void setResults(List<MeasurementQosResult> results) {
		this.results = results;
	}
}
