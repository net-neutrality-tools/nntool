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

/**
 * The roaming type.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("The roaming type.")
public enum RoamingType {
	/**
	 * No roaming.
	 */
	NO_ROAMING,
	
	/**
	 * National roaming.
	 */
	NATIONAL,
	
	/**
	 * International roaming.
	 */
	INTERNATIONAL,
	
	/**
	 * Roaming type is not supported by the platform (e.g. iOS)
	 */
	NOT_AVAILABLE,
	
	/**
	 * The roaming type is supported by the platform (e.g. android), but could not be fetched
	 */
	UNKNOWN
}
