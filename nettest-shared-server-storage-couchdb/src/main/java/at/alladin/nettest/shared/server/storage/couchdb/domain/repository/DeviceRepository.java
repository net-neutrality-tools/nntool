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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Device;
import at.alladin.nettest.spring.data.couchdb.core.query.View;
import at.alladin.nettest.spring.data.couchdb.repository.CouchDbRepository;
import at.alladin.nettest.spring.data.couchdb.core.query.Key;

/**
 * 
 * @author lb@alladin.at
 *
 */
public interface DeviceRepository extends CouchDbRepository<Device> {

	@View(
		designDocument = "Settings", viewName = "by_device", includeDocs = true, descending = true
	)
	Page<Device> getAllDevices(Pageable page);

	@View(
	    designDocument = "Settings", viewName = "by_device", includeDocs = true, descending = true,
	    keys = {
	        @Key(value = "codeName")
	    }
	)
	Device findByCodeName(@Param("codeName") String codeName);
}
