package at.alladin.nettest.service.statistic.web.api.v1;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.version.VersionResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

/**
 * Resource that returns the version number/code of this service.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/versions")
public class VersionResource {
	
	/**
	 * Get the server version.
	 * Returns the software version of the statistic server.
	 * 
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Get the server version.", notes = "Returns the software version of the statistic server.")
	@io.swagger.annotations.ApiResponses({
		//@io.swagger.annotations.ApiResponse(code = 200, message = "OK"), // TODO: message
		//@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		//@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<VersionResponse>> getVersion() {
		final VersionResponse versionResponse = new VersionResponse();
		versionResponse.setStatisticServiceVersion("0.0.0");
		
		return ResponseHelper.ok(versionResponse);
	}
}
