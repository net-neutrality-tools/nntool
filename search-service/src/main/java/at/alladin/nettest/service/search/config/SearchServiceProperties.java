package at.alladin.nettest.service.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The search service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "search-service", ignoreUnknownFields = true)
public class SearchServiceProperties {
	
}
