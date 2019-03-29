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
