package at.alladin.nettest.spring.data.couchdb.repository.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import at.alladin.nettest.couchdb.client.CouchDbClient;
import at.alladin.nettest.couchdb.client.CouchDbDatabase;
import at.alladin.nettest.couchdb.client.config.CouchDbDatabaseMapping;
import at.alladin.nettest.spring.data.couchdb.core.CouchDbOperations;
import at.alladin.nettest.spring.data.couchdb.core.CouchDbTemplate;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class RepositoryOperationsMappingFactoryBean implements FactoryBean<RepositoryOperationsMapping>, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryOperationsMappingFactoryBean.class);
	
	private CouchDbClient couchDbClient;
	private CouchDbDatabaseMapping couchDbDatabaseMapping;

	public void setCouchDbClient(CouchDbClient couchDbClient) {
		this.couchDbClient = couchDbClient;
	}
	
	public void setCouchDbDatabaseMapping(CouchDbDatabaseMapping couchDbDatabaseMapping) {
		this.couchDbDatabaseMapping = couchDbDatabaseMapping;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public RepositoryOperationsMapping getObject() throws Exception {
		final RepositoryOperationsMapping mapping = new RepositoryOperationsMapping();
		
		logger.info("Initializing CouchDB operations mapping");
		
		couchDbDatabaseMapping.getDatabases().forEach(db -> {
			logger.info("Initializing CouchDB database (name: {}, createIfNotExists: {})", db.getName(), db.isCreateIfNotExists());
			
			final CouchDbDatabase database = couchDbClient.getDatabase(db.getName(), db.isCreateIfNotExists());
			final CouchDbOperations operations = new CouchDbTemplate(database);
			
			db.getEntities().forEach(e -> {
				logger.info("Mapping CouchDB entity '{}' to database '{}'", e, db.getName());
				mapping.mapEntityName(e, operations);
			});
		});
		
		return mapping;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return RepositoryOperationsMapping.class;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(couchDbClient, "couchDbClient must not be null!");
		Assert.notNull(couchDbDatabaseMapping, "couchDbDatabaseMapping must not be null!");
	}
}
