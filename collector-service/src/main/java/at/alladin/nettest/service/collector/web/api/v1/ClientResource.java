package at.alladin.nettest.service.collector.web.api.v1;

import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.registration.RegistrationResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.settings.SettingsRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.client.settings.SettingsResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementType;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import io.swagger.annotations.ApiParam;

/**
 * This controller provides the REST API end-point relevant for all client specific calls.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/clients")
public class ClientResource {

	private final Logger logger = LoggerFactory.getLogger(ClientResource.class);

	/**
	 * Registers a new client.
	 * This resource is used to register new clients. Clients will be assigned a UUID. Terms and conditions must be accepted in the request object.
	 *
	 * @param registrationRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Registers a new client.", notes = "This resource is used to register new clients. Clients will be assigned a uuid. Terms and conditions must be accepted in the request object.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 201, message = "Created - New client created successfully. The response also contains the current settings."),
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request - Occurs if the request is malformed or if the terms and conditions aren't accepted by the client.", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<ApiResponse<RegistrationResponse>> registerClient(@ApiParam("Registration request") @RequestBody ApiRequest<RegistrationRequest> registrationApiRequest) {
		return ResponseHelper.ok(new RegistrationResponse());
	}

	/**
	 * Retrieve the current settings.
	 * This resource is used to get the current settings for this client from the server.
	 *
	 * @param uuid
	 * @param settingsApiRequest
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Retrieve the current settings.", notes = "This resource is used to get the current settings for this client from the server.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 404, message = "Not Found - If the clientUuid is not found on the server", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{clientUuid}/settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<SettingsResponse>> getSettings(
		@ApiParam(value = "The client's UUID", required = true) @PathVariable String clientUuid,
		@ApiParam(required = true) ApiRequest<SettingsRequest> settingsApiRequest) {
		return ResponseHelper.ok(new SettingsResponse());
	}

	/**
	 * Retrieve a (paginated) list of measurements made by this client.
	 * This resource returns brief information of each measurement from the requesting client.
	 *
	 * @param clientUuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Retrieve a (paginated) list of measurements made by this client.", notes = "This resource returns brief information of each measurement from the requesting client.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{clientUuid}/measurements", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<ApiPagination<BriefMeasurementResponse>>> getMeasurements(
		@ApiParam(value = "The client's UUID", required = true) @PathVariable String clientUuid,
		Pageable pageable) {

		return ResponseHelper.ok(new PageImpl<>(Arrays.asList(new BriefMeasurementResponse()), pageable, 1));
	}

	/**
	 * Disassociate all measurements of this client.
	 * All measurements of this client will be disassociated, i.e. the connection between measurement and client will be removed.
	 *
	 * @param clientUuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Disassociate all measurements of this client.", notes = "All measurements of this client will be disassociated, i.e. the connection between measurement and client will be removed.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@DeleteMapping(value = "/{clientUuid}/measurements", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DisassociateResponse>> disassociateAllMeasurements(@ApiParam(value = "The client's UUID", required = true) @PathVariable String clientUuid) {

		return ResponseEntity.ok(null);
	}

	/**
	 * Returns a measurement.
	 * Returns a measurement by uuid with either all or a custom selection of measurement types.
	 *
	 * @param clientUuid
	 * @param uuid
	 * @param includedMeasurementTypes
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns a measurement.", notes = "Returns a measurement by uuid with either all or a custom selection of measurement types.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{clientUuid}/measurements/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<FullMeasurementResponse>> getMeasurement(
		@ApiParam(value = "The client's UUID", required = true) @PathVariable String clientUuid,
		@ApiParam(value = "The measurement UUID", required = true) @PathVariable String uuid,
		@ApiParam(value = "Set of included measurement types (e.g. SPEED, QOS). If nothing is provided all measurement types are returned") @RequestParam(name = "include") Set<MeasurementType> includedMeasurementTypes) {

		logger.debug("{}", includedMeasurementTypes);

		return ResponseEntity.ok(null);
	}

	/**
	 * Get details of a measurement.
	 * Returns the measurement details, either in grouped or plain form.
	 *
	 * @param clientUuid
	 * @param uuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Get details of a measurement.", notes = "Returns the measurement details, either in grouped or plain form.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{clientUuid}/measurements/{uuid}/details", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DetailMeasurementResponse>> getMeasurementDetails(
		@ApiParam(value = "The client's UUID", required = true) @PathVariable String clientUuid,
		@ApiParam(value = "The measurement UUID", required = true) @PathVariable String uuid,
		@ApiParam(value = "Flag that indicates if the details should be grouped") @RequestParam(value = "grouped", defaultValue = "false") boolean grouped) {

		return ResponseEntity.ok(null);
	}

	/**
	 * Disassociate a single measurement.
	 * This measurement will be disassociated, i.e. the connection between measurement and client will be removed.
	 *
	 * @param clientUuid
	 * @param uuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Disassociate a single measurement.", notes = "This measurement will be disassociated, i.e. the connection between measurement and client will be removed.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@DeleteMapping(value = "/{clientUuid}/measurements/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DisassociateResponse>> deleteMeasurement(
		@ApiParam(value = "The client's UUID", required = true) @PathVariable String clientUuid,
		@ApiParam(value = "The measurement UUID", required = true) @PathVariable String uuid) {

		return ResponseEntity.ok(null);
	}
}
