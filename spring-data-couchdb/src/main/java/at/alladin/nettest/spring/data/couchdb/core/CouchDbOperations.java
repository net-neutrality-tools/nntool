package at.alladin.nettest.spring.data.couchdb.core;

import at.alladin.nettest.couchdb.client.CouchDbDatabase;
import at.alladin.nettest.couchdb.client.CouchDbQueryResult;
import at.alladin.nettest.spring.data.couchdb.repository.query.CouchDbEntityInformation;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface CouchDbOperations {

	/**
	 * The database name used for the specified class by this template.
	 * 
	 * @param entityClass must not be {@literal null}.
	 * @return
	 */
	String getDatabaseName(Class<?> entityClass);
	
	
	CouchDbDatabase getCouchDbDatabase();
	
	/////////
	
	
	//<T, S extends T> S insert(S entity, CouchDbEntityInformation<T, String> entityInformation);
	//<T, S extends T> S update(S entity, CouchDbEntityInformation<T, String> entityInformation);
	<T, S extends T> S save(S entity, CouchDbEntityInformation<T, String> entityInformation);
	
	<T, S extends T> Iterable<S> saveAll(Iterable<S> entities, CouchDbEntityInformation<T, String> entityInformation);
	
	<T> T findById(String id, Class<T> entityClass);
	
	boolean exists(final String id);
	
	<T> Iterable<T> findAll(CouchDbEntityInformation<T, String> entityInformation);
	
	<T> void remove(Object objectToRemove, CouchDbEntityInformation<T, String> entityInformation);

	<T> CouchDbQueryResult<T> query(String query, Class<T> entityClass);
	
}
