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

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import at.alladin.nettest.spring.data.couchdb.core.mapping.DocTypeHelper;

/**
 * Contains device-specific information.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains device-specific information.")
@JsonIgnoreProperties(ignoreUnknown=true)
public class Device {
	
	@Expose
	@SerializedName("_id")
	@JsonProperty("_id")
	private String id;
	
	@Expose
	@SerializedName("_rev")
	@JsonProperty("_rev")
	private String rev;
	
	@JsonProperty("docType")
	@Expose
	@SerializedName("docType") // TODO: rename to @docType
	private String docType;
	
	public Device() {
		docType = DocTypeHelper.getDocType(getClass());
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRev() {
		return rev;
	}
	
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	public String getDocType() {
		return docType;
	}
	
	/**
	 * Device code name.
	 */
	@JsonPropertyDescription("Device code name.")
	@Expose
	@SerializedName("code_name")
	@JsonProperty("code_name")
	private String codename;
	
	/**
     * The device name that is commonly known to users (e.g. Google Pixel).
	 */
	@JsonPropertyDescription("The device name that is commonly known to users (e.g. Google Pixel).")
	@Expose
	@SerializedName("full_name")
	@JsonProperty("full_name")
	private String fullname;
	
	public String getCodename() {
		return codename;
	}
	
	public void setCodename(String codename) {
		this.codename = codename;
	}
	
	public String getFullname() {
		return fullname;
	}
	
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	@Override
	public String toString() {
		return "Device [id=" + id + ", codename=" + codename + ", fullname=" + fullname + "]";
	}
}
