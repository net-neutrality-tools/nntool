package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfiguration;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementAgentRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TaskConfigurationQoSRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.TaskConfigurationSpeedRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.FullMeasurementResponseMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapReportModelMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapTaskMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.MeasurementAgentMapper;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.SettingsResponseMapper;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class CouchDbStorageService implements StorageService {

	@Autowired
	private MeasurementRepository measurementRepository;
	
	@Autowired
	private SettingsRepository settingsRepository;
	
	@Autowired
	private MeasurementAgentRepository measurementAgentRepository;
	
	@Autowired
	private TaskConfigurationSpeedRepository taskConfigurationSpeedRepository;
	
	@Autowired
	private TaskConfigurationQoSRepository taskConfigurationQoSRepository;
	
	@Autowired
	private LmapReportModelMapper lmapReportModelMapper;
	
	@Autowired
	private MeasurementAgentMapper measurementAgentMapper;
	
	@Autowired
	private SettingsResponseMapper settingsResponseMapper;
	
	@Autowired
	private FullMeasurementResponseMapper fullMeasurementResponseMapper;
	
	@Autowired
	private LmapTaskMapper lmapTaskMapper;
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#save(at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto)
	 */
	@Override
	public MeasurementResultResponse save(LmapReportDto lmapReportDto) throws StorageServiceException {
		final Measurement measurement = lmapReportModelMapper.map(lmapReportDto);
		
		measurement.setUuid(UUID.randomUUID().toString());
		measurement.setOpenDataUuid(UUID.randomUUID().toString());
		
		try {
			measurementRepository.save(measurement);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		
		final MeasurementResultResponse resultResponse = new MeasurementResultResponse();
		
		resultResponse.setUuid(measurement.getUuid());
		resultResponse.setOpenDataUuid(measurement.getOpenDataUuid());
		
		return resultResponse;
	}

	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#registerMeasurementAgent(at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest)
	 */
	@Override
	public RegistrationResponse registerMeasurementAgent(ApiRequest<RegistrationRequest> registrationRequest) throws StorageServiceException {
		final RegistrationResponse ret = new RegistrationResponse();
		final MeasurementAgent agent = measurementAgentMapper.map(registrationRequest);
		
		if (agent.getUuid() != null) {
			final MeasurementAgent dbAgent = measurementAgentRepository.findByUuid(agent.getUuid());
			if (dbAgent == null) {
				throw new StorageServiceException("invalid uuid");
			}
			ret.setAgentUuid(dbAgent.getUuid());
			if (dbAgent.getTermsAndConditionsAcceptedVersion() < agent.getTermsAndConditionsAcceptedVersion()) {
				dbAgent.setTermsAndConditionsAcceptedVersion(agent.getTermsAndConditionsAcceptedVersion());
				dbAgent.setTermsAndConditionsAcceptedTime(LocalDateTime.now(ZoneId.of("UTC")));
				try {
					measurementAgentRepository.save(dbAgent);
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new StorageServiceException();
				}
			}
		} else {
			final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
			agent.setUuid(UUID.randomUUID().toString());
			agent.setTermsAndConditionsAcceptedTime(now);
			agent.setRegistrationTime(now);
			try {
				measurementAgentRepository.save(agent);
			} catch (Exception ex) {
				throw new StorageServiceException();
			}
			ret.setAgentUuid(agent.getUuid());
		}
		
		return ret;
	}
	
	@Override
	public boolean isValidMeasurementAgentUuid(String measurementAgentUuid) throws StorageServiceException {
		final MeasurementAgent agent;
		try {
			agent = measurementAgentRepository.findByUuid(measurementAgentUuid);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		if (agent == null || !agent.isTermsAndConditionsAccepted()) {
			return false;
		}
		return true;
	}
	
	@Override
	public LmapTaskDto getTaskDto(MeasurementTypeDto type, String version) {
		TaskConfiguration taskConfig = null;
		try {
			switch (type) {
			case SPEED:
				taskConfig = taskConfigurationSpeedRepository.findByNameAndVersion(type.toString(), version);
				break;
			case QOS:
				taskConfig = taskConfigurationQoSRepository.findByNameAndVersion(type.toString(), version);
				break;
			}
			
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		if (taskConfig == null) {
			return null;
		}
		return lmapTaskMapper.map(taskConfig);
	}

	@Override
	public SettingsResponse getSettings(String settingsUuid) throws StorageServiceException {
		final Settings settings;
		try {
			settings = settingsRepository.findByUuid(settingsUuid);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		
		return settingsResponseMapper.map(settings);
		
	}
	
	@Override
	public FullMeasurementResponse getMeasurementByAgentAndMeasurementUuid(String measurementAgentUuid,
			String measurementUuid) throws StorageServiceException {
		final Measurement measurement;
		try {
			measurement = measurementRepository.findByUuid(measurementUuid);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		if (measurement == null || measurement.getAgentInfo() == null || measurementAgentUuid == null
				|| !measurementAgentUuid.equals(measurement.getAgentInfo().getUuid())) {
			throw new StorageServiceException("No measurement for agent and uuid found.");
		}
		
		return fullMeasurementResponseMapper.map(measurement);
	}
}
