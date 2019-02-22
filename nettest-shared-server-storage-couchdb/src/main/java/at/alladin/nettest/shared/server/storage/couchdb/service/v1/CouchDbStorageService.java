package at.alladin.nettest.shared.server.storage.couchdb.service.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.repository.MeasurementRepository;
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
	private LmapReportModelMapper lmapReportModelMapper;
	
	/*
	 * (non-Javadoc)
	 * @see at.alladin.nettest.shared.server.service.storage.v1.StorageService#save(at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto)
	 */
	@Override
	public void save(LmapReportDto lmapReportDto) { // TODO
		final Measurement measurement = lmapReportModelMapper.map(lmapReportDto);
		
		measurementRepository.save(measurement);
	}
}
