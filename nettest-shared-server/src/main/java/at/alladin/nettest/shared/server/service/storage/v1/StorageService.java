package at.alladin.nettest.shared.server.service.storage.v1;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface StorageService {

	void save(LmapReportDto lmapReportDto); // TODO: custom exception
	
}
