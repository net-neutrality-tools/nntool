package at.alladin.nettest.shared.server.service.storage.v1;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface StorageService {

	MeasurementResultResponse save(LmapReportDto lmapReportDto); // TODO: custom exception
	
}
