package at.alladin.nettest.service.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
public class ElasticsearchConfiguration {

	/**
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "close")
	public RestHighLevelClient elasticsearchClient() {
		final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 19200, "http")));
		return client;
	}
}
