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

package at.alladin.nettest.service.collector.opendata.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import at.alladin.nettest.shared.server.config.ElasticSearchProperties;

/**
 * The opendata collector service YAML configuration object.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@ConfigurationProperties(prefix = "opendata-collector", ignoreUnknownFields = true)
public class OpendataCollectorServiceProperties {

	private final ElasticSearchProperties elasticsearch = new ElasticSearchProperties();
	
	private final OpendataImport opendataImport = new OpendataImport();
	
	public ElasticSearchProperties getElasticsearch() {
		return elasticsearch;
	}
	
	public OpendataImport getOpendataImport() {
		return opendataImport;
	}
	
	public static class OpendataImport {
		
		private boolean enabled;
		
		private Config config = new Config();
		
		private List<Source> sources = new ArrayList<>();
    
		public boolean isEnabled() {
			return enabled;
		}
		
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		
		public Config getConfig() {
			return config;
		}
		
		public List<Source> getSources() {
			return sources;
		}
		
		public static class Config {
			
			private long batchSize = 20;
			private long batchRunLimit = 10;
			private long timeBetweenRequests = 1000;
			
			public long getBatchSize() {
				return batchSize;
			}
			
			public void setBatchSize(long batchSize) {
				this.batchSize = batchSize;
			}
			
			public long getBatchRunLimit() {
				return batchRunLimit;
			}
			
			public void setBatchRunLimit(long batchRunLimit) {
				this.batchRunLimit = batchRunLimit;
			}
			
			public long getTimeBetweenRequests() {
				return timeBetweenRequests;
			}
			
			public void setTimeBetweenRequests(long timeBetweenRequests) {
				this.timeBetweenRequests = timeBetweenRequests;
			}
		}
		
		public static class Source {

			private String name;
	        private String cron;
	        private String url;
	        
	        private boolean enabled;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getCron() {
				return cron;
			}

			public void setCron(String cron) {
				this.cron = cron;
			}

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public boolean isEnabled() {
				return enabled;
			}

			public void setEnabled(boolean enabled) {
				this.enabled = enabled;
			}
		}
    }
}
