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
	private final OpendataMeasurementImportService opendataMeasurementImportService;
	
	public OpendataImportWorker(Source source, Config config, RestTemplate restTemplate, OpendataMeasurementImportService opendataMeasurementImportService) {
		this.source = source;
		this.config = config;
		
		this.restTemplate = restTemplate;
		this.opendataMeasurementImportService = opendataMeasurementImportService;
	}
	
	//https://search-de-01.net-neutrality.tools/api/v1/measurements?page=0&size=5&sort=start_time,asc&q=start_time:["2019-11-26T13:00:00" TO now]

	@Override
	public void run() {
		final String sourceName = source.getName();
		
		if (config.getBatchRunLimit() < 1) {
			logger.debug("Skipping import for source {} because batch-run-limit must be greater than zero (current value: {}).", sourceName, config.getBatchRunLimit());
			return;
		}
		
		logger.debug("Running opendata import for source: {}, url: {}", sourceName, source.getUrl());
		
		final String latestStartTime = opendataMeasurementImportService.getLatestStartTime();
		
		ApiPagination<Map<String, Object>> currentResult = null;
		long page = 0;
		
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
				opendataMeasurementImportService.insertMeasurements(measurements);
			} catch (Exception ex) {
				logger.error("Aborting opendata import of source {} due to exception during database inserts.", sourceName, ex);
				return;
			}

			try { Thread.sleep(config.getTimeBetweenRequests()); } catch (InterruptedException e) { }
		} while (
			currentResult != null && currentResult.getContent() != null && 
			!currentResult.getContent().isEmpty() 
			&& page < currentResult.getTotalPages()
			&& page < config.getBatchRunLimit()
		); // TODO: specify timeout?
		
		logger.debug("Finished opendata import after {} page(s) for source: {}, url: {}", page, sourceName, source.getUrl()); // (took {} seconds)
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
