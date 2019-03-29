package at.alladin.nettest.couchdb.client;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface CouchDbClient {

	public CouchDbDatabase getDatabase(String name, boolean createIfNotExists);
	
	public void shutdown();
	
}
