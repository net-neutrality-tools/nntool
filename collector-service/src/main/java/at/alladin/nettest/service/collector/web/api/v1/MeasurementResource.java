package at.alladin.nettest.service.collector.web.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementInitiationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.MeasurementInitiationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import io.swagger.annotations.ApiParam;

/**
 * This controller is responsible for measurement initiation (= request) and measurement results (= response).
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementResource {

	/**
	 * Request a new measurement.
	 * This request will fetch the current measurement parameters and configuration from the server.
	 *
	 * @param measurementInitiationRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Request a new measurement.", notes = "This request will fetch the current measurement parameters and configuration from the server.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 201, message = "Created - Measurement paramaters are provided."),
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ApiResponse<MeasurementInitiationResponse>> requestMeasurement(@ApiParam("Initiation request") @RequestBody ApiRequest<MeasurementInitiationRequest> measurementInitiationApiRequest) {
		return ResponseHelper.ok(new MeasurementInitiationResponse());
	}

	/**
	 * Store measurement result.
	 * This resource retrieves finished measurement and stores them.
	 *
	 * @param measurementResultRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Store measurement result.", notes = "This resource retrieves finished measurement and stores them.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PutMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<MeasurementResultResponse>> putMeasurement(@ApiParam("Measurement result") @RequestBody ApiRequest<MeasurementResultRequest> measurementResultApiRequest) {
		return ResponseHelper.ok(new MeasurementResultResponse());
	}
}
