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
	
	<T> CouchDbQueryResult<T> query(String query, final Class<T> classType);

	CouchDbResponse remove(Object object);

	List<CouchDbResponse> bulk(List<?> objects);
	
}
