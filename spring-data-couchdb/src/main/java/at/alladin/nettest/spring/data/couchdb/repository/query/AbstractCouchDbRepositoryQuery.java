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

package at.alladin.nettest.spring.data.couchdb.repository.query;

import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public abstract class AbstractCouchDbRepositoryQuery implements RepositoryQuery {

	protected final CouchDbQueryMethod method;
	protected final CouchDbOperations operations;
	
	public AbstractCouchDbRepositoryQuery(CouchDbQueryMethod method, CouchDbOperations operations) {
	    this.method = method;
	    this.operations = operations;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.query.RepositoryQuery#getQueryMethod()
	 */
	@Override
	public QueryMethod getQueryMethod() {
		return method;
	}
}
