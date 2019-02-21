package at.alladin.nettest.spring.data.couchdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.GsonBuilder;

import at.alladin.nettest.couchdb.client.CouchDbClient;
import at.alladin.nettest.couchdb.client.cloudant.CloudantCouchDbClient;
import at.alladin.nettest.couchdb.client.config.CouchDbConnectionProperties;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public abstract class AbstractCouchDbConfiguration extends AbstractCouchDbDataConfiguration implements CouchDbConfigurer {

	protected abstract CouchDbConnectionProperties couchDbConnectionProperties();
	
	protected abstract GsonBuilder couchDbGsonBuilder();
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.config.AbstractCouchDbDataConfiguration#couchDbConfigurer()
	 */
	@Override
	protected CouchDbConfigurer couchDbConfigurer() {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.spring.data.couchdb.config.CouchDbConfigurer#couchDbClient()
	 */
	@Override
	@Bean(destroyMethod = "shutdown"/*, name = BeanNames.COUCHDB_CLIENT*/)
	public CouchDbClient couchDbClient() throws Exception {
		return CloudantCouchDbClient.build(couchDbConnectionProperties(), couchDbGsonBuilder());
	}
}
