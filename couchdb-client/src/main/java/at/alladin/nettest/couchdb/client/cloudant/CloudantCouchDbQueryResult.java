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

package at.alladin.nettest.couchdb.client.cloudant;

import java.util.List;

import com.cloudant.client.api.query.QueryResult;

import at.alladin.nettest.couchdb.client.CouchDbQueryResult;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public class CloudantCouchDbQueryResult<T> implements CouchDbQueryResult<T> {

	private final QueryResult<T> cloudantQueryResult;
	
	CloudantCouchDbQueryResult(QueryResult<T> cloudantQueryResult) {
		this.cloudantQueryResult = cloudantQueryResult;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbQueryResult#getDocs()
	 */
	@Override
	public List<T> getDocs() {
		return cloudantQueryResult.getDocs();
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbQueryResult#getWarning()
	 */
	@Override
	public String getWarning() {
		return cloudantQueryResult.getWarning();
	}
}
