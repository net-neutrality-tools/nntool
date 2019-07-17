package at.alladin.nettest.service.result.web.api.v1;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.result.config.ResultServiceProperties;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.brief.BriefMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.disassociate.DisassociateResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeneralMeasurementTypeDto;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nettest.shared.server.service.storage.v1.exception.StorageServiceException;
import io.swagger.annotations.ApiParam;

/**
 * This controller provides the REST API end-point relevant for all measurement result specific calls.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurement-agents")
public class MeasurementAgentResultResource {

	private final Logger logger = LoggerFactory.getLogger(MeasurementAgentResultResource.class);

	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ResultServiceProperties properties;
	
	/**
	 * Retrieve a (paginated) list of measurements made by this measurement agent.
	 * This resource returns brief information of each measurement from the requesting measurement agent.
	 *
	 * @param agentUuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Retrieve a (paginated) list of measurements made by this measurement agent.", notes = "This resource returns brief information of each measurement from the requesting measurement agent.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{agentUuid}/measurements", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<ApiPagination<BriefMeasurementResponse>>> getMeasurements(
		@ApiParam(value = "The measurement agent's UUID", required = true) @PathVariable String agentUuid,
		Pageable pageable) {

		if (!storageService.isValidMeasurementAgentUuid(agentUuid)) {
			return ResponseEntity.badRequest().build();
		}
		
		final List<BriefMeasurementResponse> responseList = storageService.getPagedBriefMeasurementResponseByAgentUuid(agentUuid, pageable);
		
		return ResponseHelper.ok(new PageImpl<>(responseList, pageable, 1));
	}

	/**
	 * Disassociate all measurements of this measurement agent.
	 * All measurements of this measurement agent will be disassociated, i.e. the connection between measurement and measurement agent will be removed.
	 * The measurements will no longer appear in the user's history and the measurements will no longer carry the agentUuid.
	 * This feature allows end user's with privacy concerns to use this measurement tool without fear.
	 *
	 * @param agentUuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Disassociate all measurements of this measurement agent.", notes = "All measurements of this measurement agent will be disassociated, i.e. the connection between measurement and measurement agent will be removed. The measurements will no longer appear in the user's history and the measurements will no longer carry the agentUuid. This feature allows end user's with privacy concerns to use this measurement tool without fear.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@DeleteMapping(value = "/{agentUuid}/measurements", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DisassociateResponse>> disassociateAllMeasurements(@ApiParam(value = "The measurement agent's UUID", required = true) @PathVariable String agentUuid) {
		if (!storageService.isValidMeasurementAgentUuid(agentUuid)) {
			return ResponseEntity.badRequest().build();
		}
		
		return ResponseHelper.ok(storageService.disassociateAllMeasurements(agentUuid));
	}

	/**
	 * Returns a measurement.
	 * Returns a measurement by UUID with either all or a custom selection of measurement types.
	 *
	 * @param agentUuid
	 * @param uuid
	 * @param includedMeasurementTypes
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns a measurement.", notes = "Returns a measurement by uuid with either all or a custom selection of measurement types.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{agentUuid}/measurements/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<FullMeasurementResponse>> getMeasurement(
		@ApiParam(value = "The measurement agent's UUID", required = true) @PathVariable String agentUuid,
		@ApiParam(value = "The measurement UUID", required = true) @PathVariable String uuid,
		@ApiParam(value = "Set of included measurement types (e.g. SPEED, TCP_PORT, VOIP, ...). If nothing is provided all measurement types are returned") @RequestParam(required = false, name = "include") Set<GeneralMeasurementTypeDto> includedMeasurementTypes,
		Locale locale) {

		if (!storageService.isValidMeasurementAgentUuid(agentUuid)) {
			return ResponseEntity.badRequest().build();
		}
		
		logger.debug("{}", includedMeasurementTypes);
		
		return ResponseHelper.ok(storageService.getFullMeasurementByAgentAndMeasurementUuid(agentUuid, uuid, locale));
	}

	/**
	 * Get details of a measurement.
	 * Returns the measurement details, either in grouped or plain form.
	 *
	 * @param agentUuid
	 * @param uuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Get details of a measurement.", notes = "Returns the measurement details, either in grouped or plain form.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{agentUuid}/measurements/{uuid}/details", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DetailMeasurementResponse>> getMeasurementDetails(
		@ApiParam(value = "The measurement agent's UUID", required = true) @PathVariable String agentUuid,
		@ApiParam(value = "The measurement UUID", required = true) @PathVariable String uuid,
		@ApiParam(value = "Flag that indicates if the details should be grouped") @RequestParam(value = "grouped", defaultValue = "false") boolean grouped,
		Locale locale) {

		if (!storageService.isValidMeasurementAgentUuid(agentUuid)) {
			throw new StorageServiceException("Invalid agent uuid");
		}
		
		return ResponseHelper.ok(storageService.getDetailMeasurementByAgentAndMeasurementUuid(agentUuid, uuid, properties.getSettingsUuid(), locale));
	}

	/**
	 * Disassociate a single measurement.
	 * This measurement will be disassociated, i.e. the connection between measurement and measurement agent will be removed.
	 * The measurement will no longer appear in the user's history and the measurement will no longer carry the agentUuid.
	 * This feature allows end user's with privacy concerns to use this measurement tool without fear.
	 *
	 * @param agentUuid
	 * @param uuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Disassociate a single measurement.", notes = "This measurement will be disassociated, i.e. the connection between measurement and measurement agent will be removed. The measurement will no longer appear in the user's history and the measurrment will no longer carry the agentUuid. This feature allows end user's with privacy concerns to use this measurement tool without fear.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@DeleteMapping(value = "/{agentUuid}/measurements/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DisassociateResponse>> deleteMeasurement(
		@ApiParam(value = "The measurement agent's UUID", required = true) @PathVariable String agentUuid,
		@ApiParam(value = "The measurement UUID", required = true) @PathVariable String uuid) {

		if (!storageService.isValidMeasurementAgentUuid(agentUuid)) {
			return ResponseEntity.badRequest().build();
		}
		
		return ResponseHelper.ok(storageService.disassociateMeasurement(agentUuid, uuid));
	}
}
