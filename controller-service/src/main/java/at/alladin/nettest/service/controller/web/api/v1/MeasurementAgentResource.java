package at.alladin.nettest.service.controller.web.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.controller.config.ControllerServiceProperties;
import at.alladin.nettest.service.controller.exception.MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.settings.SettingsResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import io.swagger.annotations.ApiParam;

/**
 * This controller provides the REST API end-point relevant registering measurement agents and retrieving settings.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurement-agents")
public class MeasurementAgentResource {

	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(MeasurementAgentResource.class);

	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ControllerServiceProperties controllerServiceProperties;
	
	/**
	 * Registers a new  measurement agent.
	 * This resource is used to register new measurement agents. Measurement agents will be assigned a UUID. Terms and conditions must be accepted in the request object.
	 *
	 * @param registrationRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Registers a new measurement agent.", notes = "This resource is used to register new measurement agents. Measurement agents will be assigned a UUID. Terms and conditions must be accepted in the request object.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 201, message = "Created - New client created successfully. The response also contains the current settings."),
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request - Occurs if the request is malformed or if the terms and conditions aren't accepted by the client.", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ApiResponse<RegistrationResponse>> registerMeasurementAgent(@ApiParam("Registration request") @RequestBody ApiRequest<RegistrationRequest> registrationApiRequest) {		
		logger.debug("Incoming registration request: " + registrationApiRequest.toString());
		final RegistrationRequest request = registrationApiRequest.getData();
		if (!request.isTermsAndConditionsAccepted()) {
			throw new MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException();
		}
		
		final RegistrationResponse registrationResponse;
		try {
			registrationResponse = storageService.registerMeasurementAgent(registrationApiRequest);
		} catch (StorageServiceException ex) {
			//TODO: talk about handling that differently
			throw new MeasurementAgentRegistrationTermsAndConditionsNotAcceptedException("Client has unknown uuid in registration request");
		}
		
		try {
			registrationResponse.setSettings(storageService.getSettings(controllerServiceProperties.getSettingsUuid()));
		} catch (StorageServiceException ex) {
			//we let them register even if the settings have issues
			ex.printStackTrace();
		}
		
		logger.debug("returned response: " + registrationResponse.toString());
		
		return ResponseHelper.ok(registrationResponse);
	}

	/**
	 * Retrieve the current settings.
	 * This resource is used to get the current settings for this measurement agent from the server.
	 *
	 * @param agentUuid
	 * @param settingsApiRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Retrieve the current settings.", notes = "This resource is used to get the current settings for this measurement agent from the server.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found - If the agentUuid is not found on the server", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{agentUuid}/settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<SettingsResponse>> getSettings(
		@ApiParam(value = "The measurement agent's UUID", required = true) @PathVariable String agentUuid,
		@ApiParam(required = true) ApiRequest<SettingsRequest> settingsApiRequest) {
		//TODO: shall we check if the agent exists?
		return ResponseHelper.ok(storageService.getSettings(controllerServiceProperties.getSettingsUuid()));
	}
}
