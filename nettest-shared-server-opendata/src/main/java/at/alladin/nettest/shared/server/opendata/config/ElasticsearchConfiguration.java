/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package at.alladin.nettest.shared.server.opendata.config;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Configuration
@ConditionalOnProperty(name = "collector.elasticsearch.host")
public abstract class ElasticsearchConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);
	
	@Bean
	public abstract ElasticSearchProperties elasticSearchProperties();
	
	/**
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "close", name = {"elasticSearchClient"})
	public RestHighLevelClient elasticsearchClient() {
		final ElasticSearchProperties e = elasticSearchProperties();
		
		final RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(e.getHost(), e.getPort(), e.getScheme())));
		
		logger.info("Created Elasticsearch RestHighLevelClient (to host {} on port {})", e.getHost(), e.getPort());
		
		return client;
	}
	
	@PostConstruct
	private void createIndexIfNotExists() {
		final String index = elasticSearchProperties().getIndex();
		
		try {
		
			// index
			final boolean indexExists = elasticsearchClient().indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
			
			if (indexExists) {
				logger.debug("{} index already created", index);
			} else {
				logger.debug("Creating {} index", index);
	
				final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
				
				// Fix mapping for measurements.QOS.key_to_translation_map
				createIndexRequest.mapping("{\n" + 
						"      \"dynamic_templates\": [\n" + 
						"        {\n" + 
						"          \"strings\": {\n" + 
						"            \"match_mapping_type\": \"string\",\n" + 
						"            \"path_match\": \"measurements.QOS.key_to_translation_map.*\",\n" + 
						"            \"mapping\": {\n" + 
						"              \"type\": \"object\",\n" + 
						"              \"index\": \"not_analyzed\",\n" + 
						"              \"enabled\": false\n" +
						"            }\n" + 
						"          }\n" + 
						"        }\n" + 
						"      ]\n" + 
						"    }", XContentType.JSON);
				elasticsearchClient().indices().create(createIndexRequest, RequestOptions.DEFAULT);
			}
		} catch (Exception ex) {
			logger.error("Could not create configured Elasticsearch index named {}", index, ex);
		}
	}
}
