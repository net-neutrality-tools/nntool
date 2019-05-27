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
	
	final ElasticSearchProperties elasticsearch = new ElasticSearchProperties();
	
	public ElasticSearchProperties getElasticsearch() {
		return elasticsearch;
	}
	
	/**
	 * 
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 */
	public static class ElasticSearchProperties {
		
		private String host;		
		private int port;
		private String scheme;
		
		private String index;
		private String queryTimeout; 
		
		public String getHost() {
			return host;
		}
		
		public void setHost(String host) {
			this.host = host;
		}
		
		public int getPort() {
			return port;
		}
		
		public void setPort(int port) {
			this.port = port;
		}
		
		public String getScheme() {
			return scheme;
		}
		
		public void setScheme(String scheme) {
			this.scheme = scheme;
		}
		
		public String getIndex() {
			return index;
		}
		
		public void setIndex(String index) {
			this.index = index;
		}
		
		public String getQueryTimeout() {
			return queryTimeout;
		}
		
		public void setQueryTimeout(String queryTimeout) {
			this.queryTimeout = queryTimeout;
		}
	}
}
