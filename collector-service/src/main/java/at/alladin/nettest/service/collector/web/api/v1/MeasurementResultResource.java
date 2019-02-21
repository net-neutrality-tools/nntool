package at.alladin.nettest.service.collector.web.api.v1;

import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.collector.domain.model.CouchDbMeasurement;
import at.alladin.nettest.service.collector.domain.repository.MeasurementRepository;
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

	private static final Logger logger = LoggerFactory.getLogger(MeasurementResultResource.class);
	
	@Autowired
	private MeasurementRepository measurementRepository;
	
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
		
		/////// // TODO: remove
		//logger.debug("{}", measurementRepository.count());
		
		final CouchDbMeasurement measurement = new CouchDbMeasurement();
		measurement.setUuid(UUID.randomUUID().toString());
		
		measurementRepository.save(measurement);
		
		logger.debug("{}", measurement.getRev());
		
		final CouchDbMeasurement m = measurementRepository./*findByUuid*/findByUuidAndUuidExists(measurement.getUuid());
		logger.debug("{}", m);
		
		///
		
		logger.debug("--------");
		
		logger.debug("{}", measurementRepository.findAllMangoQuery());
		
		logger.debug("--------");

		logger.debug("{}", measurementRepository.findByUuidWithMangoQueryAnnotation(measurement.getUuid()));
		
		logger.debug("--------");
		
		logger.debug("{}", measurementRepository.findByViewSingle(measurement.getUuid()));
		
		logger.debug("--------");
		
		logger.debug("{}", measurementRepository.findByViewSingleOptional(measurement.getUuid()));
		
		logger.debug("--------");
		
		logger.debug("{}", measurementRepository.findByViewList(measurement.getUuid()));
		
		logger.debug("--------");
		
		logger.debug("{}", measurementRepository.findByViewStream(measurement.getUuid()).collect(Collectors.toList()));
		
		///////
		
		return ResponseHelper.ok(new MeasurementResultResponse());
	}
}
