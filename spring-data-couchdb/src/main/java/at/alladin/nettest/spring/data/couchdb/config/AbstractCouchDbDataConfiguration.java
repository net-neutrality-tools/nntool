package at.alladin.nettest.spring.data.couchdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public abstract class AbstractCouchDbDataConfiguration extends CouchDbConfigurationSupport {

	//private static final Logger logger = LoggerFactory.getLogger(AbstractCouchDbDataConfiguration.class);
	
	/**
	 * 
	 * @return
	 */
	protected abstract CouchDbConfigurer couchDbConfigurer();

	@Bean
	public CouchDbDatabaseMapping couchDbDatabaseMapping() {
		final CouchDbDatabaseMapping mapping = new CouchDbDatabaseMapping();
		
		configureCouchDbDatabaseMapping(mapping);
		
		return mapping;
	}
	
	protected void configureCouchDbDatabaseMapping(CouchDbDatabaseMapping mapping) {
		// may be overwritten by user
	}
}
