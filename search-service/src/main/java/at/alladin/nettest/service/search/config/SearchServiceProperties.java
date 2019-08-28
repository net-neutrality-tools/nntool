package at.alladin.nettest.service.search.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 * The search service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "search-service", ignoreUnknownFields = true)
@EnableConfigurationProperties(ExportProperties.class)
public class SearchServiceProperties {
	
	@NestedConfigurationProperty
	private final ElasticSearchProperties elasticsearch = new ElasticSearchProperties();
	
	@NestedConfigurationProperty
	private final ExportProperties export = new ExportProperties();
	
	private final Search search = new Search();
	
	public ElasticSearchProperties getElasticsearch() {
		return elasticsearch;
	}
	
	public ExportProperties getExport() {
		return export;
	}
	
	public Search getSearch() {
		return search;
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class Search {
		
		private int maxPageSize = 100;
		
		public int getMaxPageSize() {
			return maxPageSize;
		}
		
		public void setMaxPageSize(int maxPageSize) {
			this.maxPageSize = maxPageSize;
		}
	}
}
