package at.alladin.nettest.service.search.config;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);
	
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
	
	@PostConstruct
	private void createIndexIfNotExists() {
		final String index = searchServiceProperties.getElasticsearch().getIndex();
		
		try {
		
			// index
			final boolean indexExists = elasticsearchClient().indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
			
			if (indexExists) {
				logger.debug("{} index already created", index);
			} else {
				logger.debug("Creating {} index", index);
	
				final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
				elasticsearchClient().indices().create(createIndexRequest, RequestOptions.DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("Could not create configured Elasticsearch index named {}", index, ex);
		}
	}
}
