package at.alladin.nettest.service.loadbalancer.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

/**
 * 
 * @author lb
 *
 */
@Service
public class MeasurementServerLoadMonitorService {
	
	private final static int DEFAULT_DELAY = 10000; //10s
	
	private final Logger logger = LoggerFactory.getLogger(MeasurementServerLoadMonitorService.class);

	@Autowired
	TaskScheduler taskScheduler;

	@Autowired
	StorageService storageService;

	Map<String, ScheduledFuture<?>> jobs = new HashMap<>();
	
	@PostConstruct
	public void init() {
		/*
		List<MeasurementServerDto> peers = storageService.getAllActiveSpeedMeasurementServers();
		if (peers != null) {
			for (MeasurementServerDto peer : peers) {
				logger.debug("Initializing load monitor for : {}, ID: {}", peer.getName(), peer.getIdentifier());
				final ScheduledFuture<?> task = taskScheduler.scheduleWithFixedDelay(new LoadRunnable(peer), DEFAULT_DELAY);
				jobs.put(peer.getIdentifier(), task);
			}
		}
		*/
	}
}
