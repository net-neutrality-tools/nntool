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
