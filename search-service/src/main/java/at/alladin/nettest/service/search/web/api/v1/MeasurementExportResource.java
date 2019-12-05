package at.alladin.nettest.service.search.web.api.v1;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.search.service.ExportService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;

/**
 * This controller is responsible for accessing and searching open-data measurement results.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementExportResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MeasurementExportResource.class);
	
	private static final String DEFAULT_EXTENSION = "csv";
	
	@Autowired
	private ExportService exportService;
	
	/**
	 * Exports a single measurement as JSON, CSV or YAML file.
	 * 
	 * @return
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Exports a single measurement as JSON, CSV or YAML file.", notes = "Exports a single measurement as JSON, CSV or YAML file.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping({"/{openDataUuid:[^\\.]+}.{extension:[^\\.]+}"})
	public void exportSingleMeasurement(@PathVariable String openDataUuid, @PathVariable String extension, @RequestParam(name = "coarse", required = false) boolean coarse, HttpServletResponse response) {
		exportService.exportSingleMeasurement(openDataUuid, coarse, determineExtension(extension), response);
	}
	
	////
	
	/**
	 * Export measurements by year and month as CSV.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year and month as CSV.", notes = "Export measurements by year and month as CSV.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year:\\d+}/{month:\\d+}")
    public void exportMonthlyData(@PathVariable Integer year, @PathVariable Integer month, HttpServletResponse response) {
		exportMonthlyData(year, month, DEFAULT_EXTENSION, response);
	}
	
	/**
	 * Export measurements by year and month as a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year and month as a file with custom extension.", notes = "Export measurements by year and month as a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year:\\d+}/{month:\\d+}.{extension:[^\\.]+}")
    public void exportMonthlyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable String extension, HttpServletResponse response) {
		exportService.exportMeasurementsByDate(year, month, null, determineExtension(extension), "zip".equals(extension), response);
	}
	
	/**
	 * Export measurements by year and month as ZIP file including a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year and month as ZIP file including a file with custom extension.", notes = "Export measurements by year and month as ZIP file including a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year:\\d+}/{month:\\d+}.{extension:[^\\.]+}.zip")
    public void exportMonthlyZippedData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable String extension, HttpServletResponse response) {
		exportService.exportMeasurementsByDate(year, month, null, extension, true, response);
	}

	/**
	 * Export measurements by year, month and day as CSV.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param response
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year, month and day as CSV.", notes = "Export measurements by year, month and day as CSV.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year:\\d+}/{month:\\d+}/{day:\\d+}")
    public void exportDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, HttpServletResponse response) {
		exportDailyData(year, month, day, DEFAULT_EXTENSION, response);
	}
	
	/**
	 * Export measurements by year, month and day as a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year, month and day as a file with custom extension.", notes = "Export measurements by year, month and day as a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year:\\d+}/{month:\\d+}/{day:\\d+}.{extension:[^\\.]+}")
    public void exportDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @PathVariable String extension, HttpServletResponse response) {
		exportService.exportMeasurementsByDate(year, month, day, determineExtension(extension), "zip".equals(extension), response);
	}
	
	/**
	 * Export measurements by year, month and day as ZIP file including a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 * @throws IOException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year, month and day as ZIP file including a file with custom extension.", notes = "Export measurements by year, month and day as ZIP file including a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year:\\d+}/{month:\\d+}/{day:\\d+}.{extension:[^\\.]+}.zip")
    public void exportZippedDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @PathVariable String extension, HttpServletResponse response) {
		exportService.exportMeasurementsByDate(year, month, day, extension, true, response);
	}
	
	////
	
	// TODO: move to export service
	private String determineExtension(String extension) {
		if (StringUtils.isEmpty(extension) || "zip".equals(extension)) {
			return DEFAULT_EXTENSION;
		}
		
		return extension;
	}
}
