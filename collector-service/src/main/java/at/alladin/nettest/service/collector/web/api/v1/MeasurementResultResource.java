package at.alladin.nettest.service.collector.web.api.v1;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import io.swagger.annotations.ApiParam;

/**
 * This controller is responsible for measurement results.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementResultResource {

	/**
	 * Store measurement result.
	 * This resource retrieves finished measurements and stores them.
	 *
	 * @param measurementResultRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Store measurement result.", notes = "This resource retrieves finished measurements and stores them.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<MeasurementResultResponse>> putMeasurement(@ApiParam("Measurement result") @RequestBody LmapReportDto measurementResultApiRequest) {
		return ResponseHelper.ok(new MeasurementResultResponse());
	}
}
