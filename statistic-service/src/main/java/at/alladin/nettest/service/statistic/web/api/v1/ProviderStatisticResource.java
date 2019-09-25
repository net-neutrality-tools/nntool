package at.alladin.nettest.service.statistic.web.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.statistic.dto.MeasurementFilterResponseDto;
import at.alladin.nettest.service.statistic.service.FilterService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

/**
 *  * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/statistics/providers")
public class ProviderStatisticResource {

	@Autowired
	FilterService filterService;
	
	@io.swagger.annotations.ApiOperation(value = "Retrieve the default list of filter elements for the providers statistics.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(path="/filters", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<MeasurementFilterResponseDto>> getFilter() {
		final MeasurementFilterResponseDto response = new MeasurementFilterResponseDto();
		response.setFilterList(filterService.getFiltersForProviderStatistics());
		return ResponseHelper.ok(response);
	}

}
