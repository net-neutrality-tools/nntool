package at.alladin.nettest.service.search.web.api.v1;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.search.service.SearchService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

/**
 * This controller is responsible for searching open-data measurement results.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementSearchResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MeasurementSearchResource.class);
	
	@Autowired
	private SearchService searchService;
	
	/**
	 * This resource returns a list of measurements.
	 *
	 * @param measurementResultRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Get measurement list.", notes = "This resource returns a list of measurements.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<ApiPagination<Map<String, Object>>>> getMeasurementList(
    	@RequestParam(name = "q", required = false) String queryString, 
    	@PageableDefault(page = 0, size = 50) @SortDefaults({ @SortDefault(sort = "end_time", direction = Sort.Direction.DESC )}) Pageable pageable
    ) {
		return ResponseHelper.ok(searchService.findAll(queryString, pageable));
	}
}
