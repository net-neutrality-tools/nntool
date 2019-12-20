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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * ASN mapping configuration used to identify a fixed-line provider.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("ASN mapping configuration used to identify a fixed-line provider.")
public class ProviderAsnMapping {

	/**
	 * The Autonomous System Number (ASN).
	 */
	@JsonPropertyDescription("The Autonomous System Number (ASN).")
	@Expose
	@SerializedName("asn")
	@JsonProperty("asn")
	private Long asn;
	
	/**
	 * The reverse DNS suffix, an optional condition for an ASN mapping (e.g.: .com).
	 */
	@JsonPropertyDescription("The reverse DNS suffix, an optional condition for an ASN mapping (e.g.: .com).")
	@Expose
	@SerializedName("condition_rdns_suffix")
	@JsonProperty("condition_rdns_suffix")
	private String conditionRdnsSuffix;
	
	public Long getAsn() {
		return asn;
	}
	
	public void setAsn(Long asn) {
		this.asn = asn;
	}
	
	public String getConditionRdnsSuffix() {
		return conditionRdnsSuffix;
	}
	
	public void setConditionRdnsSuffix(String conditionRdnsSuffix) {
		this.conditionRdnsSuffix = conditionRdnsSuffix;
	}
}
