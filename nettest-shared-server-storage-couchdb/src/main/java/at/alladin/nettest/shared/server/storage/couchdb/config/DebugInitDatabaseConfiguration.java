package at.alladin.nettest.shared.server.storage.couchdb.config;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DebugInitDatabaseConfiguration {

	@PostConstruct
	private void postConstruct() {
		
		
		
	}
}
