package at.alladin.nettest.spring.data.couchdb.config;

import at.alladin.nettest.couchdb.client.CouchDbClient;
import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface CouchDbConfigurer {

	CouchDbClient couchDbClient() throws Exception;
	
	CouchDbDatabaseMapping couchDbDatabaseMapping();
}
