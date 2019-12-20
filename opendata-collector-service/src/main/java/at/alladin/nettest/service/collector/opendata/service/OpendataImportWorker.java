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

package at.alladin.nettest.service.collector.opendata.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties.OpendataImport.Config;
import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties.OpendataImport.Source;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.server.opendata.service.OpenDataMeasurementService;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class OpendataImportWorker implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(OpendataImportWorker.class);
	
	private final Source source;
	private final Config config;
	
	private final RestTemplate restTemplate;
	private final OpenDataMeasurementService openDataMeasurementService;
	
	public OpendataImportWorker(Source source, Config config, RestTemplate restTemplate, OpenDataMeasurementService openDataMeasurementService) {
		this.source = source;
		this.config = config;
		
		this.restTemplate = restTemplate;
		this.openDataMeasurementService = openDataMeasurementService;
	}

	@Override
	public void run() {
		final String sourceName = source.getName();
		
		if (config.getBatchRunLimit() < 1) {
			logger.info("Skipping import for source {} because batch-run-limit must be greater than zero (current value: {}).", sourceName, config.getBatchRunLimit());
			return;
		}
		
		logger.info("Running opendata import for source: {}, url: {}", sourceName, source.getUrl());
		
		final String latestStartTime = openDataMeasurementService.getLatestStartTime();
		
		ApiPagination<Map<String, Object>> currentResult = null;
		long page = 0;
		long measurementCount = 0;
		
		do {
			final String urlWithParams = generateCurrentUrl(latestStartTime, page++);
		
			logger.debug("Requesting measurements from {}.", urlWithParams);

			try {
				final ResponseEntity<ApiResponse<ApiPagination<Map<String, Object>>>> resultEntity = 
						restTemplate.exchange(urlWithParams, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponse<ApiPagination<Map<String, Object>>>>() {}); // TODO: catch exception
		
				currentResult = resultEntity.getBody().getData();
			} catch (Exception ex) {
				logger.error("Aborting opendata import of source {} due to exception during HTTP request.", sourceName, ex);
				return;
			}
			
			final List<Map<String, Object>> measurements = currentResult.getContent();
			
			logger.debug("Importing {} measurements from page {} (source: {}, url: {}).", measurements.size(), page, sourceName, urlWithParams);

			try {
				openDataMeasurementService.bulkStoreOpenDataMeasurement(measurements);
			} catch (Exception ex) {
				logger.error("Aborting opendata import of source {} due to exception during database inserts.", sourceName, ex);
				return;
			}
			
			measurementCount += measurements.size();

			try { Thread.sleep(config.getTimeBetweenRequests()); } catch (InterruptedException e) { }
		} while (
			currentResult != null && currentResult.getContent() != null && 
			!currentResult.getContent().isEmpty() 
			&& page < currentResult.getTotalPages()
			&& page < config.getBatchRunLimit()
		); // TODO: specify timeout?
		
		logger.info("Finished opendata import after {} page(s) ({} measurements) for source: {}, url: {}", page, measurementCount, sourceName, source.getUrl()); // (took {} seconds)
	}

	private String generateCurrentUrl(String startTime, long page) {
		final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(source.getUrl())
				.queryParam("page", page)
				.queryParam("size", config.getBatchSize())
				.queryParam("sort", "start_time,asc");

		if (StringUtils.hasLength(startTime)) {
			//builder.queryParam("q", "start_time:[" + startTime + " TO now]");
			builder.queryParam("q", "start_time:{" + startTime + " TO now]"); // TODO: we have to be careful with 'now' and time zones!
		}
				
		return builder.build(false).toUriString();
	}
}
