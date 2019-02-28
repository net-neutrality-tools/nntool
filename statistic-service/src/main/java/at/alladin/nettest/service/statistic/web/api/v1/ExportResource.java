package at.alladin.nettest.service.statistic.web.api.v1;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.statistic.service.DataExportService;
import at.alladin.nettest.service.statistic.service.DataExportService.ExportExtension;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import io.undertow.util.BadRequestException;

/**
 * This resource is responsible for exporting measurement data of a specified time interval.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/exports")
public class ExportResource {

	@Inject
	private DataExportService dataExportService;
	
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
	@GetMapping("/{year}/{month:\\d+}")
    public void exportMonthlyData(@PathVariable Integer year, @PathVariable Integer month, HttpServletResponse response) {
		this.exportMonthlyMeasurements(year, month, ExportExtension.CSV, response, false);
	}
	
	/**
	 * Export measurements by year and month as a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year and month as a file with custom extension.", notes = "Export measurements by year and month as a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year}/{month:\\d*}.{extension:[^\\.]+}")
    public void exportMonthlyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable String extension, HttpServletResponse response) throws BadRequestException {
		final ExportExtension ext = ExportExtension.getByName(extension);
		if (ext == null) {
			throw new BadRequestException("Unsupported extension of type: " + extension);
		}
		this.exportMonthlyMeasurements(year, month, ext, response, false);
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
	@GetMapping("/{year}/{month:\\d*}.{extension:[^\\.]+}.zip")
    public void exportMonthlyZippedData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable String extension, HttpServletResponse response) throws BadRequestException {
		final ExportExtension ext = ExportExtension.getByName(extension);
		if (ext == null) {
			throw new BadRequestException("Unsupported extension of type: " + extension);
		}
		this.exportMonthlyMeasurements(year, month, ext, response, true);
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
	@GetMapping("/{year}/{month}/{day:\\d*}")
    public void exportDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, HttpServletResponse response) {
		exportDailyMeasurement(day, month, year, ExportExtension.CSV, response, false);
	}
	
	/**
	 * Export measurements by year, month and day as a file with custom extension.
	 * Supported custom extensions are CSV, JSON, YAML.
	 * 
	 * @param year
	 * @param month
	 * @param response
	 */
	@io.swagger.annotations.ApiOperation(value = "Export measurements by year, month and day as a file with custom extension.", notes = "Export measurements by year, month and day as a file with custom extension. Supported custom extensions are CSV, JSON, YAML.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping("/{year}/{month}/{day:\\d*}.{extension:[^\\.]+}")
    public void exportDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @PathVariable String extension, HttpServletResponse response) throws BadRequestException {
		final ExportExtension ext = ExportExtension.getByName(extension);
		if (ext == null) {
			throw new BadRequestException("Unsupported extension of type: " + extension);
		}
		exportDailyMeasurement(day, month, year, ext, response, false);
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
	@GetMapping("/{year}/{month}/{day:\\d*}.{extension:[^\\.]+}.zip")
    public void exportZippedDailyData(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @PathVariable String extension, HttpServletResponse response) throws BadRequestException {
		final ExportExtension ext = ExportExtension.getByName(extension);
		if (ext == null) {
			throw new BadRequestException("Unsupported extension of type: " + extension);
		}
		exportDailyMeasurement(day, month, year, ext, response, true);
	}
	
	/**
	 * Exports a single measurement as JSON, CSV or YAML file.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Exports a single measurement as JSON, CSV or YAML file.", notes = "Exports a single measurement as JSON, CSV or YAML file.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping({"/{openDataUuid:[^\\.]+}.{extension:[^\\.]+}"})
	public void exportSingleMeasurement(@PathVariable String openDataUuid, @PathVariable String extension,
										HttpServletResponse response) throws Exception {
		final ExportExtension ext = ExportExtension.getByName(extension);
		if (ext == null) {
			throw new BadRequestException("Unsupported extension of type: " + extension);
		}
		exportSingleMeasurement(openDataUuid, ext, response);
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
	@GetMapping("/{openDataUuid:[^\\.]+}")
	public void exportSingleMeasurement(@PathVariable String openDataUuid, HttpServletResponse response) {
		exportSingleMeasurement(openDataUuid,  ExportExtension.CSV, response);
	}
	
	private void exportSingleMeasurement(final String openDataUuid, final ExportExtension extension, HttpServletResponse response) {
		try {
			response.setContentType("text/" + extension.toString());
			response.setHeader("Content-disposition", "attachment; filename=" +	
					dataExportService.getExportFilename(openDataUuid, extension, false));
			dataExportService.writeExportData(response.getOutputStream(), openDataUuid, extension, false);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void exportMonthlyMeasurements (final int year, final int month, final ExportExtension extension, 
			final HttpServletResponse response, final boolean isCompress) {
		try {
			response.setContentType("text/" + extension.toString());
			response.setHeader("Content-disposition", "attachment; filename=" +	
					dataExportService.getExportFilename(month, year, extension, isCompress));
			dataExportService.writeExportData(response.getOutputStream(), month, year, extension, isCompress);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void exportDailyMeasurement (final int day, final int month, final int year,
			final ExportExtension extension, final HttpServletResponse response, final boolean isCompress) {
		try {
			response.setContentType("text/" + extension.toString());
			response.setHeader("Content-disposition", "attachment; filename=" +	
					dataExportService.getExportFilename(day, month, year, extension, isCompress));
			dataExportService.writeExportData(response.getOutputStream(), day, month, year, extension, isCompress);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
