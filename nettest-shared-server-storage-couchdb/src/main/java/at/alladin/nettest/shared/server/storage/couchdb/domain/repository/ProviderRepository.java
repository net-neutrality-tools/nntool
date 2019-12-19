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

package at.alladin.nettest.shared.server.storage.couchdb.domain.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Provider;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;
import at.alladin.nettest.spring.data.couchdb.core.query.View;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface ProviderRepository extends CouchDbRepository<Provider> {

	List<Provider> findAll();
	
	/**
	 * 
	 * @param asn
	 * @return
	 */
	@View(
		designDocument = "Settings", viewName = "provider_by_asn", includeDocs = true, descending = true,
		startKey = {
			@Key(value = "asn", highSentinel = true),
		},
		endKey = {
			@Key(value = "asn", nullValue = true),
		}
	)
	List<Provider> findByAsn(@Param("asn") Long asn);

	
	
	@View(
		designDocument = "Settings", viewName = "provider_by_mccmnc", includeDocs = true, descending = true,
		startKey = {
			@Key(value = "mcc", highSentinel = false),
			@Key(value = "mnc", highSentinel = false),
		},
		endKey = {
			@Key(value = "mcc", nullValue = false),
			@Key(value = "mnc", nullValue = false),
		}
	)
	List<Provider> findBySimMccMnc(@Param("mcc") Integer mcc, @Param("mnc") Integer mnc);
}
