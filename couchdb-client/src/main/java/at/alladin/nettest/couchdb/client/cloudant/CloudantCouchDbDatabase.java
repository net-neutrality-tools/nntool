package at.alladin.nettest.couchdb.client.cloudant;

import java.util.List;
import java.util.stream.Collectors;

import com.cloudant.client.api.Database;

import at.alladin.nettest.couchdb.client.CouchDbDatabase;
import at.alladin.nettest.couchdb.client.CouchDbQueryResult;
import at.alladin.nettest.couchdb.client.CouchDbResponse;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CloudantCouchDbDatabase implements CouchDbDatabase {

	private final Database cloudantDatabase;

	CloudantCouchDbDatabase(Database cloudantDatabase) {
		this.cloudantDatabase = cloudantDatabase;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#getImpl()
	 */
	@Override
	public Object getImpl() {
		return cloudantDatabase;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#contains(java.lang.String)
	 */
	@Override
	public boolean contains(String id) {
		return cloudantDatabase.contains(id);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#find(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> T find(Class<T> classType, String id) {
		return cloudantDatabase.find(classType, id);
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#save(java.lang.Object)
	 */
	@Override
	public CouchDbResponse save(Object object) {
		return new CloudantCouchDbResponse(cloudantDatabase.save(object));
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#update(java.lang.Object)
	 */
	@Override
	public CouchDbResponse update(Object object) {
		return new CloudantCouchDbResponse(cloudantDatabase.update(object));
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#query(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> CouchDbQueryResult<T> query(String query, Class<T> classType) {
		return new CloudantCouchDbQueryResult<>(cloudantDatabase.query(query, classType));
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#remove(java.lang.Object)
	 */
	@Override
	public CouchDbResponse remove(Object object) {
		return new CloudantCouchDbResponse(cloudantDatabase.remove(object));
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.couchdb.client.CouchDbDatabase#bulk(java.util.List)
	 */
	@Override
	public List<CouchDbResponse> bulk(List<?> objects) {
		return cloudantDatabase.bulk(objects).stream().map(CloudantCouchDbResponse::new).collect(Collectors.toList());
	}
}
