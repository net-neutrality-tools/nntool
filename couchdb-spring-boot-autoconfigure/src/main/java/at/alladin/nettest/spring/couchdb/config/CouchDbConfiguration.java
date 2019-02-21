package at.alladin.nettest.spring.couchdb.config;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.gson.GsonBuilder;

import at.alladin.nettest.couchdb.client.CouchDbClient;
import at.alladin.nettest.couchdb.client.cloudant.CloudantCouchDbClient;
import at.alladin.nettest.couchdb.client.config.CouchDbConnectionProperties;
import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public class CouchDbConfiguration {
	
	private final Logger logger = LoggerFactory.getLogger(CouchDbConfiguration.class);

	private CouchDbProperties couchDbProperties;
	
	public CouchDbConfiguration(CouchDbProperties couchDbProperties) {
		this.couchDbProperties = couchDbProperties;
		
		logger.debug("init CouchDbConfiguration");
	}
	
	@Primary
	@Bean
	public GsonBuilder couchDbGsonBuilder() {
		return new GsonBuilder();
	}
	
	@Primary
	@Bean(destroyMethod = "shutdown")
	public CouchDbClient couchDbClient() throws MalformedURLException {
		final CouchDbConnectionProperties properties = couchDbProperties.getConnection();
		return CloudantCouchDbClient.build(properties, couchDbGsonBuilder());
	}
	
	@Primary
	@Bean
	public CouchDbDatabaseMapping couchDbDatabaseMapping() {
		final CouchDbDatabaseMapping databaseMapping = new CouchDbDatabaseMapping();
		
		couchDbProperties.getDatabases().forEach(db -> {
			databaseMapping.getDatabases().add(db);
		});
		
		return databaseMapping;
	}
}
