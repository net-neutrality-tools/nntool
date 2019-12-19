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
 * Extends the EmbeddedNetworkType with an additional uuid for database access of that NetworkType.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Extends the EmbeddedNetworkType with an additional uuid for database access of that NetworkType.")
public class NetworkType extends EmbeddedNetworkType {

	/**
	 * Network type id as it gets returned by the Android API (serves as primary key).
	 */
	@JsonPropertyDescription("Network type id as it gets returned by the Android API (serves as primary key).")
	@Expose
	@SerializedName("id")
	@JsonProperty("id")
	private Long id;
	
}
