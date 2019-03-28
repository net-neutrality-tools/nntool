package at.alladin.nettest.service.controller.web.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.controller.service.MeasurementConfigurationService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import io.swagger.annotations.ApiParam;

/**
 * This controller is responsible for measurement initiation.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementInitiationResource {

	@Autowired
	private StorageService storageService;
	
	@Autowired
	private MeasurementConfigurationService measurementConfigurationService;
	
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
	public ResponseEntity<?> requestMeasurement(@ApiParam("Initiation request") @RequestBody LmapControlDto measurementInitiationRequest) {
		
		if (measurementInitiationRequest.getAgent() == null || 
				measurementInitiationRequest.getAgent().getAgentId() == null) {
			//TODO: do we hide that in production?
			return ResponseEntity.badRequest().body("No agent provided");
		}
		try {
			if (!storageService.isValidMeasurementAgentUuid(measurementInitiationRequest.getAgent().getAgentId())) {
				return ResponseEntity.badRequest().body("Invalid agent provided");
			}
		} catch (StorageServiceException ex) {
			return ResponseEntity.badRequest().body("Invalid agent provided");
		}
		
		if (measurementInitiationRequest.getCapabilities() == null || 
				measurementInitiationRequest.getCapabilities().getTasks() == null ||
				measurementInitiationRequest.getCapabilities().getTasks().size() == 0) {
			return ResponseEntity.badRequest().body("No capabilities provided");
		}
		
		// TODO: load balancing
		final LmapControlDto lmapControlDto = measurementConfigurationService.getLmapControlDtoForCapabilities(measurementInitiationRequest.getCapabilities());
		lmapControlDto.setAdditionalRequestInfo(measurementInitiationRequest.getAdditionalRequestInfo());
		lmapControlDto.setAgent(measurementInitiationRequest.getAgent());
		lmapControlDto.setCapabilities(measurementInitiationRequest.getCapabilities());
		
		//measurementInitiationRequest.getAgent().getAgentId();
		
		
		
		return ResponseEntity.ok(lmapControlDto);
	}
}
