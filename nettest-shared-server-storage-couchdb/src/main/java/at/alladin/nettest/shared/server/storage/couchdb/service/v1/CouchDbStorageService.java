package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.SettingsRepository;
import at.alladin.nettest.shared.server.storage.couchdb.mapper.v1.LmapReportModelMapper;

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
	private LmapReportModelMapper lmapReportModelMapper;
	
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
		// TODO: registration
		
		return null;
	}

	@Override
	public SettingsResponse getSettings(String settingsUuid) throws StorageServiceException {
		final Settings settings = settingsRepository.findByUuid(settingsUuid);
		
		final SettingsResponse settingsResponse = null;
		
		return settingsResponse;
	}
}
