package at.alladin.nettest.spring.couchdb.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import at.alladin.nettest.couchdb.client.config.CouchDbConnectionProperties;
import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "spring.couchdb")
public class CouchDbProperties {

	private final CouchDbConnectionProperties connection = new CouchDbConnectionProperties();
	
	private final List<CouchDbDatabaseMapping.Database> databases = new ArrayList<>();
	
	public CouchDbConnectionProperties getConnection() {
		return connection;
	}
	
	public List<CouchDbDatabaseMapping.Database> getDatabases() {
		return databases;
	}
}
