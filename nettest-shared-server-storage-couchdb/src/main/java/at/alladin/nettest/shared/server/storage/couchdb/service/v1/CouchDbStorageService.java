package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.QoSMeasurementSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementAgentRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementServerRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.QoSMeasurementObjectiveRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.BriefMeasurementResponseMapper;
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
	private QoSMeasurementObjectiveRepository qosMeasurementObjectiveRepository;
	
	@Autowired
	private MeasurementServerRepository measurementServerRepository;
	
	@Autowired
	private QoSEvaluationService qosEvaluationService;
	
	@Autowired
	private DetailMeasurementService detailMeasurementService;
	
	@Autowired
	private LmapReportModelMapper lmapReportModelMapper;
	
	@Autowired
	private MeasurementAgentMapper measurementAgentMapper;
	
	@Autowired
	private SettingsResponseMapper settingsResponseMapper;
	
	@Autowired
	private FullMeasurementResponseMapper fullMeasurementResponseMapper;
	
	@Autowired
	private BriefMeasurementResponseMapper briefMeasurementResponseMapper;
	
	@Autowired
	private LmapTaskMapper lmapTaskMapper;
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#save(at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto)
	 */
	@Override
	public MeasurementResultResponse save(LmapReportDto lmapReportDto) throws StorageServiceException {
		final String agentUuid = lmapReportDto.getAgentId();
		if (agentUuid == null || !isValidMeasurementAgentUuid(agentUuid)) {
			throw new StorageServiceException("Invalid user agent id");
		}
		
		final Measurement measurement = lmapReportModelMapper.map(lmapReportDto);
		
		measurement.setUuid(UUID.randomUUID().toString());
		measurement.setOpenDataUuid(UUID.randomUUID().toString());
		measurement.setSubmitTime(LocalDateTime.now(ZoneId.of("UTC")));
		
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
			final MeasurementAgent dbAgent;
			try {
				dbAgent = measurementAgentRepository.findByUuid(agent.getUuid());
			} catch (Exception ex) {
				throw new StorageServiceException("invalid uuid");
			}
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
	public LmapTaskDto getTaskDto(final MeasurementTypeDto type, final String settingsUuid) {
		try {
			final Settings settings = settingsRepository.findByUuid(settingsUuid);
			
			switch (type) {
			case SPEED:
				final SpeedMeasurementSettings speedSettings = (SpeedMeasurementSettings) settings.getMeasurements().get(type);
				//TODO: load balancing needs to select correct measurement server
				final LmapTaskDto ret = lmapTaskMapper.map(settings, 
						measurementServerRepository.findByUuid(speedSettings.getSpeedMeasurementServerUuid()), type.toString());
				return ret;
			case QOS:
				final List<QoSMeasurementObjective> qosObjectiveList = qosMeasurementObjectiveRepository.findAllByEnabled(true);
				
				if (settings.getMeasurements() != null && settings.getMeasurements().containsKey(MeasurementTypeDto.QOS)) {
					final QoSMeasurementSettings qosSettings = (QoSMeasurementSettings) settings.getMeasurements().get(MeasurementTypeDto.QOS);
					return lmapTaskMapper.map(settings, measurementServerRepository.findByUuid(
							qosSettings.getQosServerUuid()), qosObjectiveList, type.toString());
				} else {
					return lmapTaskMapper.map(settings, null, qosObjectiveList, type.toString());
				}
				
			default:
				return null;
			}
			
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
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
	public List<BriefMeasurementResponse> getPagedBriefMeasurementResponseByAgentUuid (final String measurementAgentUuid, 
			final Pageable pageable) {
		final List<Measurement> measurementList = measurementRepository.findByAgentInfoUuid(measurementAgentUuid, pageable);
		return briefMeasurementResponseMapper.map(measurementList);
	}
	
	@Override
	public FullMeasurementResponse getFullMeasurementByAgentAndMeasurementUuid(String measurementAgentUuid,
			String measurementUuid) throws StorageServiceException {
		final Measurement measurement = obtainMeasurement(measurementAgentUuid, measurementUuid);
		
		final FullMeasurementResponse ret = fullMeasurementResponseMapper.map(measurement);
		
		//evaluate the qos stuff 
		final QoSMeasurement qosMeasurement = (QoSMeasurement) measurement.getMeasurements().get(MeasurementTypeDto.QOS);
		if (qosMeasurement != null) {
			//TODO: forward qosMeasurement to mapper
			final FullQoSMeasurement fullQosMeasurement = qosEvaluationService.evaluateQoSMeasurement(qosMeasurement);
			ret.getMeasurements().put(MeasurementTypeDto.QOS, fullQosMeasurement);
		}
		
		return ret;
	}
	
	@Override
	public DetailMeasurementResponse getDetailMeasurementByAgentAndMeasurementUuid (String measurementAgentUuid, String measurementUuid, final String settingsUuid) throws StorageServiceException {
		final Measurement measurement = obtainMeasurement(measurementAgentUuid, measurementUuid);
		//TODO: default settings?
		final Settings settings = settingsRepository.findByUuid(settingsUuid);
		return detailMeasurementService.groupResult(measurement, settings.getSpeedtestDetailGroups(),
				Locale.ENGLISH, 10000);//detailMeasurementResponseMapper.map(measurement);
	}
	
	@Override
	public DisassociateResponse disassociateMeasurement(final String agentUuid, final String measurementUuid) throws StorageServiceException {
		final Measurement measurement;
		try {
			measurement = measurementRepository.findByUuid(measurementUuid);
		} catch (Exception ex) {
			throw new StorageServiceException("Unknown measurement");
		}
		if (measurement.getAgentInfo().getUuid() == null) {
			throw new StorageServiceException("Measurement already disassociated");
		}
		if (!agentUuid.equals(measurement.getAgentInfo().getUuid())) {
			throw new StorageServiceException("Invalid agent/measurement uuid pair");
		}
		disassociateMeasurement(measurement);
		try {
			measurementRepository.save(measurement);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		return new DisassociateResponse();
	}
	
	@Override
	public DisassociateResponse disassociateAllMeasurements(final String agentUuid) throws StorageServiceException {
		try {
			final List<Measurement> measurementList = measurementRepository.findByAgentInfoUuid(agentUuid);
			measurementList.forEach(m -> disassociateMeasurement(m));
			measurementRepository.saveAll(measurementList);
		} catch (Exception ex) {
			throw new StorageServiceException(ex);
		}
		return new DisassociateResponse();
	}
	
	private void disassociateMeasurement (final Measurement toAnonymize) {
		if (toAnonymize.getAgentInfo() != null) {
			toAnonymize.getAgentInfo().setUuid(null);
		}
	}
	
	private Measurement obtainMeasurement (final String measurementAgentUuid, final String measurementUuid) throws StorageServiceException {
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
		return measurement;
	}
}
