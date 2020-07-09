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
	
	private String settingsUuid;
	
	@NestedConfigurationProperty
	private final ElasticSearchProperties elasticsearch = new ElasticSearchProperties();
	
	@NestedConfigurationProperty
	private final ExportProperties export = new ExportProperties();
	
	private final Search search = new Search();
	
	public String getSettingsUuid() {
		return settingsUuid;
	}
	
	public void setSettingsUuid(String settingsUuid) {
		this.settingsUuid = settingsUuid;
	}
	
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
