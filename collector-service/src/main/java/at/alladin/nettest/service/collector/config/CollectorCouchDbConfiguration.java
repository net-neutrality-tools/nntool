package at.alladin.nettest.service.collector.config;

import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.spring.data.couchdb.repository.config.EnableCouchDbRepositories;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
@EnableCouchDbRepositories("at.alladin.nettest.service.collector.domain.repository")
public class CollectorCouchDbConfiguration {
	
}
