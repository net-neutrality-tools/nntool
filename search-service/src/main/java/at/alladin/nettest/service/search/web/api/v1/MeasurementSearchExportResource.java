package at.alladin.nettest.service.search.web.api.v1;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.search.service.ExportService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.server.helper.swagger.ApiPageable;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * This extra resource is needed because Spring always adds a leading slash to @RequestMapping values and we want to use /measurements.{extension}.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1")
public class MeasurementSearchExportResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MeasurementSearchExportResource.class);
	
	@Autowired
	private ExportService exportService;
	
	@io.swagger.annotations.ApiOperation(value = "Export a paginated and searched list of open-data measurements.", notes = "This resource exports brief information of each queried open-data measurement.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@ApiPageable
	@GetMapping("measurements.{extension:[^\\.]+}")
    public void exportSearchedMeasurementList(
    	@ApiParam(value = "The export extension", required = false) @PathVariable String extension,
    	@ApiParam(value = "The full text search string", required = false) @RequestParam(name = "q", required = false) String queryString, 
    	@ApiIgnore @PageableDefault(page = 0, size = 50) @SortDefaults({ @SortDefault(sort = "end_time", direction = Sort.Direction.DESC )}) Pageable pageable, 
    	HttpServletResponse response
    ) {	
		exportService.exportMeasurementsBySearchQuery(queryString, pageable, extension, false, response);
	}
	
	@io.swagger.annotations.ApiOperation(value = "Export a paginated and searched list of open-data measurements.", notes = "This resource exports brief information of each queried open-data measurement.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@ApiPageable
	@GetMapping("measurements.{extension:[^\\.]+}.zip")
    public void exportSearchedMeasurementListZipped(
    	@ApiParam(value = "The export extension", required = false) @PathVariable String extension,
    	@ApiParam(value = "The full text search string", required = false) @RequestParam(name = "q", required = false) String queryString, 
    	@ApiIgnore @PageableDefault(page = 0, size = 50) @SortDefaults({ @SortDefault(sort = "end_time", direction = Sort.Direction.DESC )}) Pageable pageable, 
    	HttpServletResponse response
    ) {	
		exportService.exportMeasurementsBySearchQuery(queryString, pageable, extension, true, response);
	}
}
