package at.alladin.nettest.service.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 * The collector service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "collector", ignoreUnknownFields = true)
public class CollectorServiceProperties {

	final ElasticSearchProperties elasticsearch = new ElasticSearchProperties();
	
	private String systemUuid;
    
	public ElasticSearchProperties getElasticsearch() {
		return elasticsearch;
	}

    public String getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }
}
