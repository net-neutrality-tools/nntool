package at.alladin.nettest.service.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 * The search service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "search-service", ignoreUnknownFields = true)
public class SearchServiceProperties {
	
	final ElasticSearchProperties elasticsearch = new ElasticSearchProperties();
	
	public ElasticSearchProperties getElasticsearch() {
		return elasticsearch;
	}
}
