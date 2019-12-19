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

package at.alladin.nettest.shared.server.service.storage.v1;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.LoadBalancingSettingsDto;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface StorageService {

	MeasurementResultResponse save(LmapReportDto lmapReportDto, String systemUuid) throws StorageServiceException;

	MeasurementResultResponse save(LmapReportDto lmapReportDto) throws StorageServiceException; // TODO: custom exception
	
	RegistrationResponse registerMeasurementAgent(ApiRequest<RegistrationRequest> registrationRequest) throws StorageServiceException; // TODO: custom exception
	
	boolean isValidMeasurementAgentUuid(String measurementAgentUuid) throws StorageServiceException;
	
	SettingsResponse getSettings(String settingsUuid) throws StorageServiceException; // TODO: custom exception
	
	LmapTaskDto getTaskDto(MeasurementTypeDto type, LmapCapabilityTaskDto capability, String settingsUuid, boolean useIPv6) throws StorageServiceException; // TODO: add client info to fetch personalized settings 

	FullMeasurementResponse getFullMeasurementByAgentAndMeasurementUuid(String measurementAgentUuid, String measurementUuid, Locale locale) throws StorageServiceException;
	
	DetailMeasurementResponse getDetailMeasurementByAgentAndMeasurementUuid(String measurementAgentUuid, String measurementUuid, String settingsUuid, Locale locale) throws StorageServiceException;
	
	Page<BriefMeasurementResponse> getPagedBriefMeasurementResponseByAgentUuid(String measurementAgentUuid, 
			Pageable pageable) throws StorageServiceException;
	
	DisassociateResponse disassociateMeasurement(String agentUuid, String measurementUuid) throws StorageServiceException;
	
	DisassociateResponse disassociateAllMeasurements(String agentUuid) throws StorageServiceException;
	
	SpeedMeasurementPeerResponse getSpeedMeasurementPeers(ApiRequest<SpeedMeasurementPeerRequest> speedMeasurementPeerRequest) throws StorageServiceException;
	
	Map<Locale, Map<String, String>> getTranslations();
	
  List<MeasurementServerDto> getAllActiveSpeedMeasurementServers() throws StorageServiceException;
	
	MeasurementServerDto getSpeedMeasurementServerByPublicIdentifier(final String identifier);
	
	LoadBalancingSettingsDto getLoadBalancingSettings(final String settingsUuid);
}
