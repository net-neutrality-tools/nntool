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
