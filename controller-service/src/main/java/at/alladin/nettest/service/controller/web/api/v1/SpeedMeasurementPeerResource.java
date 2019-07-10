package at.alladin.nettest.service.controller.web.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.peer.SpeedMeasurementPeerResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import io.swagger.annotations.ApiParam;

/**
 * This controller is responsible for speed measurement peers.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/speed-measurement-peers")
public class SpeedMeasurementPeerResource {
	
	@Autowired
	private StorageService storageService;
	
	@io.swagger.annotations.ApiOperation(value = "Request a list available speed measurement peers.", notes = "This request will fetch the available speed measurement peers from the server.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 401, message = "Forbidden", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<SpeedMeasurementPeerResponse>> requestSpeedMeasurementPeers(@ApiParam("Speed measurement peer request") ApiRequest<SpeedMeasurementPeerRequest> speedMeasurementPeerRequest) {
		// TODO: return 401 if feature is not enabled
		// TODO: check agent uuid?
		
		return ResponseHelper.ok(storageService.getSpeedMeasurementPeers(speedMeasurementPeerRequest));
	}
}
