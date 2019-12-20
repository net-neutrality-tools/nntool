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

package at.alladin.nettest.service.loadbalancer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
				report.setMeasurementServerIdentifier(peer.getIdentifier());
				reports.put(peer.getIdentifier(), report);
			}
		}
	}
	
	public List<LoadApiReport> getReportList() {
		return new ArrayList<LoadApiReport>(reports.values());
	}
	
	public LoadApiReport getReportForMeasurementServer(final String identifier) {
		return reports.get(identifier);
	}
		
	public MeasurementServerDto getNextAvailableMeasurementPeer(final String preferredIdentifier) {
		String id = null; 
		if (preferredIdentifier != null) {
			final LoadApiReport report = reports.get(preferredIdentifier);
			if (report != null) {
				if (report.getFailesSinceLastAttempt() <= properties.getFailsAllowed() 
						&& report.getLastResponse() != null) {
					final LoadApiResponse response = report.getLastResponse();
					if (!response.getIsOverloaded()) {
						id = preferredIdentifier;
					}
				}
			}
		}
		
		if (id == null) {
			//we are still missing a valid measurement server
			for (final Entry<String, LoadApiReport> e : reports.entrySet()) {
				final LoadApiReport report = e.getValue();
				if (report != null) {
					if (report.getFailesSinceLastAttempt() <= properties.getFailsAllowed() 
							&& report.getLastResponse() != null) {
						final LoadApiResponse response = report.getLastResponse();
						if (!response.getIsOverloaded()) {
							id = e.getKey();
							break;
						}
					}
				}	
			}
		}
		
		if (id != null) {
			//valid measurement server found
			return storageService.getSpeedMeasurementServerByPublicIdentifier(id);
		}
		
		return null;
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
				report.setMeasurementServerIdentifier(loadCallable.getPeer().getIdentifier());
				reports.put(report.getMeasurementServerIdentifier(), report);
			}
			
			logger.debug("Fetching Load report for measurement peer '{}'", report.getMeasurementServerIdentifier());
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
