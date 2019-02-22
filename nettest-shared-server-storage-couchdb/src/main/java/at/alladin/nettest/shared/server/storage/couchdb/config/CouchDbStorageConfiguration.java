package at.alladin.nettest.shared.server.storage.couchdb.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.spring.data.couchdb.repository.config.EnableCouchDbRepositories;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@EnableCouchDbRepositories("at.alladin.nettest.shared.server.storage.couchdb.domain.repository")
@ComponentScan({
	"at.alladin.nettest.shared.server.storage.couchdb",
})
@Configuration
public class CouchDbStorageConfiguration {
	
}
