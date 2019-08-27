package at.alladin.nettest.service.collector.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public class DataSourceConfiguration {

	@Configuration
	@ConditionalOnProperty(name = "spring.datasource.url")
	@Import(DataSourceAutoConfiguration.class)
	protected static class EmbeddedDatabaseConfiguration {

	}
}
