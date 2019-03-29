package at.alladin.nettest.couchdb.client.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbEntityDatabaseMapping {

	private Map<String, String> entityDatabaseMap = new HashMap<>();
	
	public CouchDbEntityDatabaseMapping map(String entity, String database) {
		entityDatabaseMap.put(entity, database);
		return this;
	}
	
	public String getDatabaseByEntity(String entity) {
		return entityDatabaseMap.get(entity);
	}
}
