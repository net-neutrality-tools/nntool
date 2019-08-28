package at.alladin.nettest.service.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 * Configuration class which creates an {@link RestHighLevelClient} client to access the Elasticsearch database. 
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public class ElasticsearchConfiguration {

	@Autowired
	private SearchServiceProperties searchServiceProperties;
	
	/**
	 * Creates the Elasticsearch client bean.
	 * 
	 * @return the Elasticsearch client bean.
	 */
	@Bean(destroyMethod = "close")
	public RestHighLevelClient elasticsearchClient() {
		final ElasticSearchProperties e = searchServiceProperties.getElasticsearch();
		
		final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(e.getHost(), e.getPort(), e.getScheme())));
		return client;
	}
}
