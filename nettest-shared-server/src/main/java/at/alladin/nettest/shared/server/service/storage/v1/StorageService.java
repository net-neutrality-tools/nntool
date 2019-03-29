package at.alladin.nettest.shared.server.service.storage.v1;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface StorageService {

	MeasurementResultResponse save(LmapReportDto lmapReportDto) throws StorageServiceException; // TODO: custom exception
	
	RegistrationResponse registerMeasurementAgent(ApiRequest<RegistrationRequest> registrationRequest) throws StorageServiceException; // TODO: custom exception
	
	boolean isValidMeasurementAgentUuid(String measurementAgentUuid) throws StorageServiceException;
	
	SettingsResponse getSettings(String settingsUuid) throws StorageServiceException; // TODO: custom exception
	
	LmapTaskDto getTaskDto (MeasurementTypeDto type, String version) throws StorageServiceException; // TODO: add client info to fetch personalized settings 

	FullMeasurementResponse getMeasurementByAgentAndMeasurementUuid (String measurementAgentUuid, String measurementUuid) throws StorageServiceException;
	
}
