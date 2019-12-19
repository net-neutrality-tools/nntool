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

import com.cloudant.client.api.model.Response;

import at.alladin.nettest.couchdb.client.CouchDbResponse;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CloudantCouchDbResponse implements CouchDbResponse {

	private final Response cloudantResponse;
	
	CloudantCouchDbResponse(Response cloudantResponse) {
		this.cloudantResponse = cloudantResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbResponse#getId()
	 */
	@Override
	public String getId() {
		return cloudantResponse.getId();
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbResponse#getRev()
	 */
	@Override
	public String getRev() {
		return cloudantResponse.getRev();
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbResponse#getError()
	 */
	@Override
	public String getError() {
		return cloudantResponse.getError();
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbResponse#getReason()
	 */
	@Override
	public String getReason() {
		return cloudantResponse.getReason();
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbResponse#getStatusCode()
	 */
	@Override
	public int getStatusCode() {
		return cloudantResponse.getStatusCode();
	}
}
