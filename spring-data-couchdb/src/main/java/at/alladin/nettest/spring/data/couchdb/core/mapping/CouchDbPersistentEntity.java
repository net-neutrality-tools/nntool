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

package at.alladin.nettest.spring.data.couchdb.core.mapping;

import org.springframework.data.mapping.PersistentEntity;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public interface CouchDbPersistentEntity<T> extends PersistentEntity<T, CouchDbPersistentProperty> {

	/**
	 * Returns the database the entity shall be persisted to.
	 * 
	 * @return
	 */
	String getDatabaseName();
	
}
