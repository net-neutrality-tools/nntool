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

package at.alladin.nettest.couchdb.client;

import java.util.List;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface CouchDbDatabase {

	//CouchDbClient getCouchDbClient();
	
	Object getImpl();
	
	boolean contains(String id);

	<T> T find(Class<T> classType, String id);

	CouchDbResponse save(Object object);
	
	CouchDbResponse update(Object object);
	
	<T> CouchDbQueryResult<T> query(String query, final Class<T> classType);

	CouchDbResponse remove(Object object);

	List<CouchDbResponse> bulk(List<?> objects);
	
}
