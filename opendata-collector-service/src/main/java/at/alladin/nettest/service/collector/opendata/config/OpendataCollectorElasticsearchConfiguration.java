package at.alladin.nettest.service.collector.opendata.config;

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
@ConditionalOnProperty(name = "opendata-collector.elasticsearch.host")
public class OpendataCollectorElasticsearchConfiguration extends ElasticsearchConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(OpendataCollectorElasticsearchConfiguration.class);
	
	@Autowired
	private OpendataCollectorServiceProperties opendataCollectorServiceProperties;
	
	@Bean
	public ElasticSearchProperties elasticSearchProperties() {
		return opendataCollectorServiceProperties.getElasticsearch();
	}
}
