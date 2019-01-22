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

package at.alladin.nettest.shared.model.qos;

import com.google.gson.annotations.Expose;

/**
 * 
 * @author bp
 *
 */
public class QosMeasurementDescription {

	/**
	 * 
	 */
	@Expose
    private String key; // test.desc.voip
    
    /**
     * 
     */
	@Expose
    private String value; // Simulated VoIP call with a duration of %PARAM voip_objective_call_duration 1000000 1 f% ms
	
	/**
	 * 
	 */
	@Expose
	private String lang; // en

	/**
	 * 
	 */
	public QosMeasurementDescription() {
		
	}

	/**
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 
	 * @param key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * 
	 * @param lang
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
}
