package at.alladin.nettest.service.loadbalancer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.loadbalancer.config.LoadbalancerServiceProperties;
import at.alladin.nettest.service.loadbalancer.dto.LoadApiReport;
import at.alladin.nettest.service.loadbalancer.dto.LoadApiResponse;
import at.alladin.nettest.service.loadbalancer.service.MeasurementServerLoadService.LoadCallable;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author lb
 *
 */
@Service
public class MeasurementServerLoadMonitorService {
	
	private final Logger logger = LoggerFactory.getLogger(MeasurementServerLoadMonitorService.class);

	@Autowired
	TaskScheduler taskScheduler;

	@Autowired
	StorageService storageService;
	
	@Autowired
	LoadbalancerServiceProperties properties;
	
	Map<String, ScheduledFuture<?>> jobs = new HashMap<>();
	
	Map<String, LoadApiReport> reports = new HashMap<>(); 
	
	@PostConstruct
	public void init() {
		List<MeasurementServerDto> peers = storageService.getAllActiveSpeedMeasurementServers();
		logger.debug("Peers: {}", peers);
		if (peers != null) {
			for (MeasurementServerDto peer : peers) {
				if (peer.getLoadApiUrl() == null || peer.getLoadApiSecretKey() == null) {
					continue;
				}
				
				logger.debug("Initializing load monitor for : {}, ID: {}, with delay: {}ms", 
						peer.getName(), peer.getIdentifier(), properties.getDelay());
				final ScheduledFuture<?> task = taskScheduler.scheduleWithFixedDelay(
						new LoadRunnable(peer), properties.getDelay());
				jobs.put(peer.getIdentifier(), task);
				
				final LoadApiReport report = new LoadApiReport();
				report.setLastAttempt(-1L);
				report.setLastSuccessfulAttempt(-1L);
				reports.put(peer.getIdentifier(), report);
			}
		}
	}
	
	public LoadApiReport getReportForMeasurementServer(final String identifier) {
		return reports.get(identifier);
	}
	
	private final class LoadRunnable implements Runnable {
		
		final LoadCallable loadCallable;
		
		public LoadRunnable(final MeasurementServerDto peer) {
			this.loadCallable = new LoadCallable(peer);
		}

		@Override
		public void run() {
			LoadApiReport report = reports.get(loadCallable.getPeer().getIdentifier());
			if (report == null) {
				report = new LoadApiReport();
				reports.put(loadCallable.getPeer().getIdentifier(), report);
			}
			
			logger.debug("Fetching Load report for measurement peer '{}'", loadCallable.getPeer().getIdentifier());
			report.setLastAttempt(System.currentTimeMillis());
			
			boolean requestHasFailed = true;
			
			try {
				final LoadApiResponse response = loadCallable.call();
				if (response != null) {
					report.setLastResponse(response);
					requestHasFailed = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (requestHasFailed) {
				report.setFailesSinceLastAttempt(report.getFailesSinceLastAttempt()+1);
			}
			else {
				report.setFailesSinceLastAttempt(0);
				report.setLastSuccessfulAttempt(report.getLastAttempt());
			}
			
			logger.debug("Load report: {}", report);
		}
	}
}
