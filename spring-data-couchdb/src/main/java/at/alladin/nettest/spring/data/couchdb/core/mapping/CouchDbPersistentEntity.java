package at.alladin.nettest.spring.data.couchdb.core.mapping;

import org.springframework.data.mapping.PersistentEntity;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T>
 */
public interface CouchDbPersistentEntity<T> extends PersistentEntity<T, CouchDbPersistentProperty> {

	/**
	 * Returns the database the entity shall be persisted to.
	 * 
	 * @return
	 */
	String getDatabaseName();
	
}
