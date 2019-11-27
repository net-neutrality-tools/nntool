package at.alladin.nettest.service.collector.opendata.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties;
import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties.OpendataImport;
import at.alladin.nettest.service.collector.opendata.config.OpendataCollectorServiceProperties.OpendataImport.Source;
import at.alladin.nettest.shared.server.opendata.service.OpenDataMeasurementService;

/**
 *
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@Service
@ConditionalOnProperty(name = "opendata-collector.opendata-import.enabled")
public class OpendataImportSchedulingService {

	private static final Logger logger = LoggerFactory.getLogger(OpendataImportSchedulingService.class);

	@Autowired
	private OpendataCollectorServiceProperties opendataCollectorServiceProperties;

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private OpenDataMeasurementService openDataMeasurementService;
	
	@Autowired
	private TaskScheduler taskScheduler;

	private final Map<String, ScheduledFuture<?>> jobs = new ConcurrentHashMap<>();

	@PostConstruct
	public void postConstruct() {
		final OpendataImport opendataImport = opendataCollectorServiceProperties.getOpendataImport();
		if (opendataImport == null) {
			return;
		}
		
		final List<OpendataImport.Source> sources = opendataImport.getSources();
		if (sources == null || sources.isEmpty()) {
			return;
		}
		
		if (!openDataMeasurementService.areOpenDataDatabasesAvailable()) {
			return;
		}
		
		sources.stream()
			.filter(Source::isEnabled)
			.forEach(source -> {
				logger.debug("Initializing device import for: {}, using cron: {}", source.getName(), source.getCron());
				
				final ScheduledFuture<?> task = taskScheduler.schedule(
					new OpendataImportWorker(source, opendataImport.getConfig(), restTemplate, openDataMeasurementService), 
					new CronTrigger(source.getCron())
				);
				
				jobs.put(source.getName(), task);
			});
	}
}
