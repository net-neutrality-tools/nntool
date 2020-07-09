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

package at.alladin.nettest.service.search.service;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.search.config.SearchServiceProperties;
import at.alladin.nettest.shared.server.model.ServerSettings;

/**
 * This service is responsible for getting the settings from the Elasticsearch database.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class SettingsService {

	private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);
	
	private static final String SETTINGS_INDEX = "nntool_settings";
	
	@Autowired
	private SearchServiceProperties searchServiceProperties;
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;	
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public ServerSettings getSettings() {
		final GetRequest getRequest = new GetRequest(SETTINGS_INDEX, "settings_" + searchServiceProperties.getSettingsUuid());
		
		try {
			final GetResponse getResponse = elasticsearchClient.get(getRequest, RequestOptions.DEFAULT);
			
			final Map<String, Object> resultMap = getResponse.getSourceAsMap();
			
			return objectMapper.convertValue(resultMap, ServerSettings.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
