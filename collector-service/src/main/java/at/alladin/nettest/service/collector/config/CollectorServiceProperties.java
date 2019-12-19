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
	
	private String settingsUuid;
    
	public ElasticSearchProperties getElasticsearch() {
		return elasticsearch;
	}

    public String getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }
    
    public String getSettingsUuid() {
		return settingsUuid;
	}
    
    public void setSettingsUuid(String settingsUuid) {
		this.settingsUuid = settingsUuid;
	}
}
