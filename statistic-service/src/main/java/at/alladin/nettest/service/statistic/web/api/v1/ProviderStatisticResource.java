package at.alladin.nettest.service.statistic.web.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.statistic.dto.ProviderFilterRequestDto;
import at.alladin.nettest.service.statistic.dto.ProviderFilterResponseDto;
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
	public ResponseEntity<ApiResponse<ProviderFilterResponseDto>> getFilters() {
		final ProviderFilterResponseDto response = new ProviderFilterResponseDto();
		response.setFilterList(filterService.getFiltersForProviderStatistics(null));
		return ResponseHelper.ok(response);
	}

	@io.swagger.annotations.ApiOperation(value = "Retrieve an updated list of filter elements for the providers statistics.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PostMapping(path="/filters", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<ProviderFilterResponseDto>> updateFilters(@RequestBody ProviderFilterRequestDto request) {
		final ProviderFilterResponseDto response = new ProviderFilterResponseDto();
		response.setFilterList(filterService.getFiltersForProviderStatistics(request));
		return ResponseHelper.ok(response);
	}
}
