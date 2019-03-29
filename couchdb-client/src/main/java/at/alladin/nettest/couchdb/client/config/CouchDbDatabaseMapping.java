package at.alladin.nettest.couchdb.client.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class CouchDbDatabaseMapping {

	private final List<Database> databases = new ArrayList<>();
	
	public List<Database> getDatabases() {
		return databases;
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class Database {
		
		private String name;
		private boolean createIfNotExists;
		private final List<String> entities = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public boolean isCreateIfNotExists() {
			return createIfNotExists;
		}

		public void setCreateIfNotExists(boolean createIfNotExists) {
			this.createIfNotExists = createIfNotExists;
		}
		
		public List<String> getEntities() {
			return entities;
		}
	}
}
