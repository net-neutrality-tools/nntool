// TODO: license, inspired by https://github.com/spring-projects/spring-data-couchbase/blob/master/src/main/java/org/springframework/data/couchbase/repository/config/RepositoryOperationsMapping.java
package at.alladin.nettest.spring.data.couchdb.repository.config;

import java.util.HashMap;
import java.util.Map;

import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;
import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class RepositoryOperationsMapping {
	
	// TODO: should we have a default database/operations?

	private final Map<String, CouchDbOperations> byRepository = new HashMap<>();
	private final Map<String, CouchDbOperations> byEntity = new HashMap<>();
	
	public RepositoryOperationsMapping() {
		
	}
	
	public RepositoryOperationsMapping(CouchDbDatabaseMapping couchDbDatabaseMapping) {
		//couchDbEntityDatabaseMapping.getDatabaseByEntity("Measurement");
	}
	
	
	public RepositoryOperationsMapping mapRepository(Class<?> repositoryInterface, CouchDbOperations operations) {
		byRepository.put(repositoryInterface.getName(), operations);
		return this;
	}
	
	public RepositoryOperationsMapping mapEntity(Class<?> entityClass, CouchDbOperations operations) {
		byEntity.put(entityClass.getName(), operations);
		return this;
	}
	
	
	public RepositoryOperationsMapping mapEntityName(String entityName, CouchDbOperations operations) {
		byEntity.put(entityName, operations);
		return this;
	}
	
	/**
	 * 
	 * @param repositoryInterface
	 * @param entityClass
	 * @return Returns null if no database was found in the maps.
	 */
	public CouchDbOperations resolve(Class<?> repositoryInterface, Class<?> entityClass) {
		// Try to find CouchDbOperations by repository, if not found try by entityClass.
		return byRepository.getOrDefault(repositoryInterface.getName(), byEntity.getOrDefault(entityClass.getName(), byEntity.get(entityClass.getSimpleName())));
	}
}
