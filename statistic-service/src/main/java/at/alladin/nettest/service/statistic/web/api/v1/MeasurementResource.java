package at.alladin.nettest.service.statistic.web.api.v1;

import java.util.Arrays;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeneralMeasurementTypeDto;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import io.swagger.annotations.ApiParam;

/**
 * This resource gives the clients access to open data measurement results.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementResource {
	
	/**
	 * Retrieve a paginated and searched list of open-data measurements.
	 * This resource returns brief information of each queried open-data measurement.
	 * 
	 * @param searchQuery
	 * @param pageable
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Retrieve a paginated and searched list of open-data measurements.", notes = "This resource returns brief information of each queried open-data measurement.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE) // TODO: include/exclude measurement types?
	public ResponseEntity<ApiResponse<ApiPagination<BriefMeasurementResponse>>> getMeasurements(@RequestParam(name = "search", defaultValue = "*") String searchQuery, Pageable pageable) {
		// TODO: max page size
		// TODO: order
		
		return ResponseHelper.ok(new PageImpl<>(Arrays.asList(new BriefMeasurementResponse()), pageable, 1));
	}
	
	/**
	 * Returns an open-data measurement.
	 * Returns a measurement by open-data UUID with either all or a custom selection of measurement types.
	 * 
	 * @param openDataUuid
	 * @param includedMeasurementTypes
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns an open-data measurement.", notes = "Returns a measurement by open-data UUID with either all or a custom selection of measurement types.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/measurements/{openDataUuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<FullMeasurementResponse>> getMeasurement(
		@ApiParam(value = "The open-data UUID", required = true) @PathVariable String openDataUuid, 
		@ApiParam(value = "Set of included measurement types (e.g. SPEED, TCP_PORT, VOIP, ...). If nothing is provided all measurement types are returned") @RequestParam(name = "include") Set<GeneralMeasurementTypeDto> includedMeasurementTypes) {
		
		return ResponseEntity.ok(null); // TODO
	}
	
	/**
	 * Get details of an open-data measurement.
	 * Returns the open-data measurement details, either in grouped or plain form.
	 *
	 * @param openDataUuid
	 * @param grouped
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Get details of an open-data measurement.", notes = "Returns the open-data measurement details, either in grouped or plain form.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	// TODO: raw/grouped
	@GetMapping(value = "/measurements/{openDataUuid}/details", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DetailMeasurementResponse>> getMeasurementDetails(
		@ApiParam(value = "The open-data UUID", required = true) @PathVariable String openDataUuid, 
		@ApiParam(value = "Flag that indicates if the details should be grouped") @RequestParam(value = "grouped", defaultValue = "false") boolean grouped) {
		
		return ResponseEntity.ok(null); // TODO
	}
}
