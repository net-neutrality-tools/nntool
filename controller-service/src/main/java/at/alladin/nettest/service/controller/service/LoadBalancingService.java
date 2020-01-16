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

package at.alladin.nettest.service.controller.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.LoadBalancingSettingsDto;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author lb@alladin.at
 *
 */
@Service
public class LoadBalancingService {

	private final static String QUERY_VAR_PREFERRED_ID = "preferredId";
	
	private final Logger logger = LoggerFactory.getLogger(LoadBalancingService.class);
	
	@Autowired
	private StorageService storageService;
	
	private LoadBalancingSettingsDto settings;
	
	/**
	 * 
	 * @param settingsUuid
	 * @param preferredId
	 * @return
	 */
	public MeasurementServerDto getNextAvailableMeasurementServer(final String settingsUuid, final String preferredId) {
		LoadBalancingSettingsDto settings = fetchSettings(settingsUuid);
		if (settings == null) {
				return null;
		}
		
		if (settings.getNextFreeUrl() == null) {
			//TODO: if no load balancer is configured, return default server????
			//return storageService.getTaskDto(type, capability, settingsUuid)
			return null;
		}
			
		final RestTemplate restTemplate = new RestTemplate();
		
		final List<MediaType> acceptableMediaTypes = new ArrayList<>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
		acceptableMediaTypes.add(new MediaType("text", "javascript"));
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(acceptableMediaTypes);
		
		final HttpEntity<?> httpEntity = new HttpEntity<>(headers);
		
		try {
			ResponseEntity<ApiResponse<MeasurementServerDto>> responseEntity = restTemplate.exchange(settings.getNextFreeUrl(), 
					HttpMethod.GET, httpEntity, new ParameterizedTypeReference<ApiResponse<MeasurementServerDto>>() { });
			
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				final ApiResponse<MeasurementServerDto> response = responseEntity.getBody();
				return response != null ? response.getData() : null;
			}
		}
		catch (final Exception e) {
			logger.info("Error fetching info from load balancing service: [{}] {}", settings.getNextFreeUrl(), e.getLocalizedMessage());
			//e.printStackTrace();
		}

		return null;

	}
	
	private LoadBalancingSettingsDto fetchSettings(final String settingsUuid) {
		return storageService.getLoadBalancingSettings(settingsUuid);
	}
}
