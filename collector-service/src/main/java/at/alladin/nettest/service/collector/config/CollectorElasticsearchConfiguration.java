package at.alladin.nettest.service.collector.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;
import at.alladin.nettest.shared.server.opendata.config.ElasticsearchConfiguration;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
@ConditionalOnProperty(name = "collector.elasticsearch.host")
public class CollectorElasticsearchConfiguration extends ElasticsearchConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(CollectorElasticsearchConfiguration.class);
	
	@Autowired
	private CollectorServiceProperties collectorServiceProperties;
	
	@Bean
	public ElasticSearchProperties elasticSearchProperties() {
		return collectorServiceProperties.getElasticsearch();
	}
}
