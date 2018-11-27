package at.alladin.nettest.service.statistic.web.api.v1;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;

/**
 * This resource is response for exporting measurement data of a specified time interval.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/exports")
public class ExportResource {

	/**
	 * Export measurements by year and month as CSV.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year and month as CSV.", notes = "Export measurements by year and month as CSV.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year}/{month}")
    public void exportMonthlyData(@PathVariable Integer year, @PathVariable Integer month, HttpServletResponse response) {
		exportMonthlyData(year, month, "csv", response);
	}
	
	/**
	 * Export measurements by year and month as ZIP file including a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year and month as ZIP file including a file with custom extension.", notes = "Export measurements by year and month as ZIP file including a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year}/{month}.{extension}.zip")
    public void exportMonthlyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable String extension, HttpServletResponse response) {
		
	}
	
	/**
	 * Export measurements by year, month and day as CSV.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param response
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year, month and day as CSV.", notes = "Export measurements by year, month and day as CSV.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year}/{month}/{day}")
    public void exportDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, HttpServletResponse response) {
		exportDailyData(year, month, day, "csv", response);
	}
	
	/**
	 * Export measurements by year, month and day as ZIP file including a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year, month and day as ZIP file including a file with custom extension.", notes = "Export measurements by year, month and day as ZIP file including a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year}/{month}/{day}.{extension}.zip")
    public void exportDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @PathVariable String extension, HttpServletResponse response) {
		
	}

	/**
	 * Exports a single measurement as CSV.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Exports a single measurement as CSV.", notes = "Exports a single measurement as CSV.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{openDataUuid}")
	public void exportSingleMeasurement(@PathVariable String openDataUuid, HttpServletResponse response) {
		exportSingleMeasurement(openDataUuid, "csv", response);
	}
	
	/**
	 * Exports a single measurement as CSV, JSON or YAML file.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Exports a single measurement as CSV, JSON or YAML file.", notes = "Exports a single measurement as CSV, JSON or YAML file.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{openDataUuid}.{extension}")
	public void exportSingleMeasurement(@PathVariable String openDataUuid, @PathVariable String extension, HttpServletResponse response) {
		
	}
}
