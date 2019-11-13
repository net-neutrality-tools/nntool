package at.alladin.nettest.service.controller.web.api.v1;

import java.net.Inet6Address;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.controller.exception.GeneralBadRequestException;
import at.alladin.nettest.service.controller.service.MeasurementConfigurationService;
import at.alladin.nettest.service.controller.service.MeasurementDeviceInformationService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapAgentDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.server.helper.IpAddressHelper;
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

	private static final Logger logger = LoggerFactory.getLogger(MeasurementInitiationResource.class);
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private MeasurementConfigurationService measurementConfigurationService;

	@Autowired
	private MeasurementDeviceInformationService measurementDeviceInformationService;
	
	/**
	 * Request a new measurement.
	 * This request will fetch the current measurement parameters and configuration from the server.
	 *
	 * @param measurementInitiationRequest
	 * @return
	 * @throws UnknownHostException 
	 */
	@io.swagger.annotations.ApiOperation(value = "Request a new measurement.", notes = "This request will fetch the current measurement parameters and configuration from the server.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 201, message = "Created - Measurement paramaters are provided."),
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> requestMeasurement(@ApiParam("Initiation request") @RequestBody LmapControlDto measurementInitiationRequest,
			HttpServletRequest request) throws UnknownHostException {
		
		final LmapAgentDto agentDto = measurementInitiationRequest.getAgent();
		
		if (agentDto == null || agentDto.getAgentId() == null) {
			//TODO: do we hide that in production?
			throw new GeneralBadRequestException("No agent provided");
		}
		try {
			if (!storageService.isValidMeasurementAgentUuid(agentDto.getAgentId())) {
				throw new GeneralBadRequestException("Invalid agent provided");
			}
		} catch (StorageServiceException ex) {
			throw new GeneralBadRequestException("Invalid agent provided");
		}
		
		final LmapCapabilityDto capabilityDto = measurementInitiationRequest.getCapabilities();
		
		if (capabilityDto == null || capabilityDto.getTasks() == null || capabilityDto.getTasks().size() == 0) {
			throw new GeneralBadRequestException("No capabilities provided");
		}
		
		measurementDeviceInformationService.fillDeviceInformation(measurementInitiationRequest.getAdditionalRequestInfo(), request);
		
		////
		
		boolean useIPv6 = IpAddressHelper.extractIpAddressFromHttpServletRequest(request) instanceof Inet6Address;
		logger.info("Measurement initiation with (useIPv6 = {})", useIPv6);
		
		// TODO: load balancing
		final LmapControlDto lmapControlDto = measurementConfigurationService.getLmapControlDtoForCapabilities(capabilityDto, useIPv6);
		lmapControlDto.setAdditionalRequestInfo(measurementInitiationRequest.getAdditionalRequestInfo());
		lmapControlDto.setAgent(agentDto);
		
		//measurementInitiationRequest.getAgent().getAgentId();
		
		return ResponseEntity.ok(lmapControlDto);
	}
}
