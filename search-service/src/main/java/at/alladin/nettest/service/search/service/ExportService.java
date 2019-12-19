package at.alladin.nettest.service.search.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

import at.alladin.nettest.service.search.config.ExportProperties;
import at.alladin.nettest.service.search.config.ExportProperties.Prefix;
import at.alladin.nettest.service.search.config.ExportProperties.Zip.AdditionalZipFile;
import at.alladin.nettest.service.search.exception.BadExportRequestException;
import at.alladin.nettest.service.search.exception.ExportException;
import at.alladin.nettest.service.search.exception.MeasurementNotFoundException;
import at.alladin.nettest.service.search.helper.CoarseResultHelper;
import at.alladin.nettest.service.search.helper.ExportExtension;

/**
 * This service is responsible for all data exports.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Service
public class ExportService {

	private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
	
	@Autowired
	private ExportProperties exportProperties;
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public void exportSingleMeasurement(String openDataUuid, boolean coarse, String extension, HttpServletResponse response) {
		if (StringUtils.isEmpty(openDataUuid)) {
			throw new BadExportRequestException("open_data_uuid is required."); 
		}
		
		final ExportExtension exportExtension = getExportExtension(extension);
		
		Map<String, Object> measurement = searchService.findOneByOpenDataUuid(openDataUuid);
		
		if (measurement == null) {
			throw new MeasurementNotFoundException("Measurement with open_data_uuid " + openDataUuid + " not found.");
		}
		
		if (coarse) { 
			measurement = CoarseResultHelper.makeCoarseResult(measurement, objectMapper);
		}
		
		final String filename = determineExportFilename(openDataUuid, coarse, extension);
		
		try {
			write(Collections.singletonList(measurement), filename, exportExtension, false, response, coarse);
		} catch (IOException ex) {
			throw new ExportException("Could not export single measurement with open_data_uuid " + openDataUuid + ".", ex);
		}
	}
	
	public void exportMeasurementsBySearchQuery(String queryString, Pageable pageable, String extension, boolean shouldBeZipped, HttpServletResponse response) {
		if (StringUtils.isEmpty(queryString)) {
			//throw new BadExportRequestException("Search query string is required.");
			queryString = "*";
		}
		
		final ExportExtension exportExtension = getExportExtension(extension);
		
		final String filename = determineExportFilenameForQuery(queryString, pageable, extension, shouldBeZipped);
		
		final Page<Map<String, Object>> page = searchService.findAll(queryString, pageable, exportProperties.getMaxPageSize());
		final List<Map<String, Object>> data = page.getContent();
		
		try {
			write(data, filename, exportExtension, shouldBeZipped, response);
		} catch (IOException ex) {
			throw new ExportException("Could not export measurements.", ex);
		}
	}
	
	public void exportMeasurementsByDate(Integer year, Integer month, Integer day, String extension, boolean shouldBeZipped, HttpServletResponse response) {
		if (year == null || month == null) {
			throw new BadExportRequestException("Both year and month have to be set.");
		}

		final ExportExtension exportExtension = getExportExtension(extension);
		
		YearMonth yearMonth;
		try {
			yearMonth = YearMonth.of(year, month);
		} catch (DateTimeException ex) {
			throw new BadExportRequestException("Year and month have to be valid.", ex);
		}
		
		if (day != null) {
			if (!yearMonth.isValidDay(day)) {
				throw new BadExportRequestException("Day " + day + " is not valid for year " + year + " and month " + month + ".");
			}
		}
		
		final String filename = determineExportFilename(year, month, day, extension, shouldBeZipped);
	
		final List<Map<String, Object>> data = searchService.findByDateRange(year, month, day);
		
		try {
			write(data, filename, exportExtension, shouldBeZipped, response);
		} catch (IOException ex) {
			throw new ExportException("Could not export measurements.", ex);
		}
	}
	
	////
	
	private ExportExtension getExportExtension(String extension) {
		if (StringUtils.isEmpty(extension)) {
			throw new BadExportRequestException("Extension must be provided.");
		}
		
		final ExportExtension exportExtension = ExportExtension.getByName(extension);
		if (exportExtension == null) {
			throw new BadExportRequestException("Extension \"" + extension + "\" is not supported.");
		}
		
		return exportExtension;
	}
	
	////
	
	private String determineExportFilename(String openDataUuid, boolean coarse, String extension/*, boolean shouldBeZipped*/) {
		final StringBuilder filenameBuilder = new StringBuilder();
		
		final Prefix prefix = exportProperties.getPrefix();
		
		addOptionalValue(filenameBuilder, prefix.getGlobal(), null, "_");
		addOptionalValue(filenameBuilder, prefix.getSingleResult(), null, "_");
		
		if (coarse) {
			filenameBuilder.append("coarse_");
		}
		
		filenameBuilder.append(openDataUuid);
		
		addExtension(filenameBuilder, extension, true, /*shouldBeZipped*/ false);
		
		return filenameBuilder.toString();
	}
	
	private String determineExportFilename(Integer year, Integer month, Integer day, String extension, boolean shouldBeZipped) {
		final StringBuilder filenameBuilder = new StringBuilder();
		
		final Prefix prefix = exportProperties.getPrefix();
		
		addOptionalValue(filenameBuilder, prefix.getGlobal(), null, "_");
		addOptionalValue(filenameBuilder, prefix.getDateRange(), null, "_");
		
		filenameBuilder.append(year);
		filenameBuilder.append("-");
		filenameBuilder.append(String.format("%02d", month));
		
		addOptionalValue(filenameBuilder, day, "-", null);
		
		addExtension(filenameBuilder, extension, true, shouldBeZipped);
		
		return filenameBuilder.toString();
	}
	
	private String determineExportFilenameForQuery(String queryString, Pageable pageable, String extension, boolean shouldBeZipped) {
		final StringBuilder filenameBuilder = new StringBuilder();
		
		final Prefix prefix = exportProperties.getPrefix();
		
		addOptionalValue(filenameBuilder, prefix.getGlobal(), null, "_");
		addOptionalValue(filenameBuilder, prefix.getSearchResult(), null, "_");
		
		// TODO: add parts from query?
		
		addExtension(filenameBuilder, extension, true, shouldBeZipped);
		
		return filenameBuilder.toString();
	}
	
	private String getCurrentUtcTimestampString() {
		return DateTimeFormatter
					.ofPattern("uuuu_MM_dd_HH_mm_ss")
					.withZone(ZoneId.of("UTC"))
					.format(Instant.now());
	}
	
	////
	
	private void addOptionalValue(StringBuilder builder, Object optionalValue, String prefix, String suffix) {
		if (optionalValue == null) {
			return;
		}
		
		if (StringUtils.hasLength(prefix)) {
			builder.append(prefix);
		}
		
		builder.append(optionalValue);
		
		if (StringUtils.hasLength(suffix)) {
			builder.append(suffix);
		}
	}
	
	private void addExtension(StringBuilder builder, String extension, boolean addTimestamp, boolean shouldBeZipped) {
		if (addTimestamp) {
			builder.append("__");
			builder.append(getCurrentUtcTimestampString());
		}
		
		builder.append(".");
		builder.append(extension.toLowerCase());
		
		if (shouldBeZipped) {
			builder.append(".zip");
		}
	}
	
	private void writeResponseHeader(ExportExtension extension, String filename, HttpServletResponse response) {
		response.setContentType(extension.getContentType());
		response.setHeader("Content-disposition", "attachment; filename=" +	filename);
	}
	
	private void write(List<Map<String, Object>> data, String filename, ExportExtension extension, boolean shouldBeZipped, HttpServletResponse response) throws IOException {
		this.write(data, filename, extension, shouldBeZipped, response, false);
	}
	
	private void write(List<Map<String, Object>> data, String filename, ExportExtension extension, boolean shouldBeZipped, HttpServletResponse response, boolean coarse) throws IOException {
		writeResponseHeader(extension, filename, response);
		
		final OutputStream outputStream;
		
		if (shouldBeZipped) {
			outputStream = prepareZip(filename, response.getOutputStream());
        } else {
        	outputStream = response.getOutputStream();
        }
		
		extension.getWriter().write(data, exportProperties, extension, outputStream, coarse);
		
		outputStream.flush();
	}
	
	private ZipOutputStream prepareZip(String filename, OutputStream outputStream) throws IOException {
		final ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
		
		if (exportProperties != null) {
			final List<AdditionalZipFile> additionalFiles = exportProperties.getZip().getAdditionalFiles();
			if (additionalFiles != null && additionalFiles.size() > 0) {
				additionalFiles.stream()
					.filter(f -> {
						return StringUtils.hasLength(f.getTitle()) && StringUtils.hasLength(f.getLocation());
					}).forEach(f -> {
						try {
							final File file = ResourceUtils.getFile(f.getLocation());
						
							final ZipEntry zeLicense = new ZipEntry(f.getTitle());
							zipOutputStream.putNextEntry(zeLicense);
			            
							Files.copy(file, zipOutputStream);
						} catch (Exception ex) {
							logger.error("Could not add file (title: {}, location: {}) to zip file.", f.getTitle(), f.getLocation(), ex);
						}
					});
			}
		}
        
        final ZipEntry zeExportFile = new ZipEntry(filename.replace(".zip", ""));
        zipOutputStream.putNextEntry(zeExportFile);
        
        return zipOutputStream;
	}
}
