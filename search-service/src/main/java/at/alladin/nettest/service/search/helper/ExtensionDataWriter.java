package at.alladin.nettest.service.search.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import at.alladin.nettest.service.search.config.ExportProperties;

/**
 * Interface which needs to be implemented for every file extension used in export.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public interface ExtensionDataWriter {

	void write(List<Map<String, Object>> data, ExportProperties exportProperties, ExportExtension ext, OutputStream outputStream) throws IOException;
}
