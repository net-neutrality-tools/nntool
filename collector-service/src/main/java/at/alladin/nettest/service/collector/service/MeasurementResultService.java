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

package at.alladin.nettest.service.collector.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.service.collector.config.CollectorServiceProperties;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.opendata.service.OpenDataMeasurementService;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class MeasurementResultService {

	private static final Logger logger = LoggerFactory.getLogger(MeasurementResultService.class);
	
	@Autowired
	private CollectorServiceProperties collectorServiceProperties;
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private OpenDataMeasurementService openDataMeasurementService;
	
	/**
	 * 
	 * @param lmapReportDto
	 * @return
	 * @throws StorageServiceException
	 */
	public MeasurementResultResponse saveResult(LmapReportDto lmapReportDto) throws StorageServiceException {
		final String systemUuid = collectorServiceProperties.getSystemUuid();
		
		// Store measurement result into CouchDB
		final MeasurementResultResponse resultResponse = storageService.save(lmapReportDto, systemUuid);
		
		final String uuid = resultResponse.getUuid();
		final String openDataUuid = resultResponse.getOpenDataUuid();
		
		logger.info("Saved result (uuid: {}, open_data_uuid: {})", uuid, openDataUuid);
		
		if (openDataMeasurementService.areOpenDataDatabasesAvailable()) {
			final String agentUuid = lmapReportDto.getAgentId();
			
			// TODO: don't evaluate QoS the same way it is evaluated for users.
			final FullMeasurementResponse measurementDto = storageService.getFullMeasurementByAgentAndMeasurementUuid(agentUuid, uuid, Locale.ENGLISH);
			
			if (measurementDto != null) {
				openDataMeasurementService.storeOpenDataMeasurement(measurementDto);
			}
		}
		
		return resultResponse;
	}
}
