package at.alladin.nettest.service.search.web.api.v1;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.search.helper.CoarseResultHelper;
import at.alladin.nettest.service.search.service.SearchService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import at.alladin.nettest.shared.server.helper.swagger.ApiPageable;
import at.alladin.nettest.shared.server.service.GroupedMeasurementService;
import at.alladin.nettest.shared.server.service.SpeedtestDetailGroup;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * This controller is responsible for accessing and searching open-data measurement results.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/measurements")
public class MeasurementSearchResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MeasurementSearchResource.class);
	
	@Autowired
	private SearchService searchService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private GroupedMeasurementService groupedMeasurementService;
	
	/**
	 * Retrieve a paginated and searched list of open-data measurements.
	 * This resource returns brief information of each queried open-data measurement.
	 * 
	 * @param @SortDefault
	 * @param pageable
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Retrieve a paginated and searched list of open-data measurements.", notes = "This resource returns brief information of each queried open-data measurement.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@ApiPageable
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ApiResponse<ApiPagination<Map<String, Object>>>> getSearchedMeasurementList(
    	@ApiParam(value = "The full text search string", required = false) @RequestParam(name = "q", required = false) String queryString, 
    	@ApiIgnore @PageableDefault(page = 0, size = 50) @SortDefaults({ @SortDefault(sort = "end_time", direction = Sort.Direction.DESC )}) Pageable pageable
    ) {	
		// TODO: BriefMeasurementResponse or FullMeasurementResponse?
		
		return ResponseHelper.ok(searchService.findAll(queryString, pageable));
	}
	
	/**
	 * Returns an open-data measurement.
	 * Returns a measurement by open-data UUID with either all or a custom selection of measurement types.
	 * 
	 * @param openDataUuid
	 * @param includedMeasurementTypes
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Returns an open-data measurement.", notes = "Returns a measurement by open-data UUID with either all or a custom selection of measurement types.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{openDataUuid:[^\\.]+}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<Map<String, Object>>> getMeasurement(
		@ApiParam(value = "The open-data UUID", required = true) @PathVariable String openDataUuid, 
		@RequestParam(name = "coarse", required = false) boolean coarse
		/*@ApiParam(value = "Set of included measurement types (e.g. SPEED, TCP_PORT, VOIP, ...). If nothing is provided all measurement types are returned") @RequestParam(name = "include", required = false) Set<GeneralMeasurementTypeDto> includedMeasurementTypes*/) {
		
		// TODO: remove measurement types not included in includedMeasurementTypes!
		
		Map<String, Object> opendataMeasurement = searchService.findOneByOpenDataUuid(openDataUuid);
		
		if (coarse) { 
			opendataMeasurement = CoarseResultHelper.makeCoarseResult(opendataMeasurement, objectMapper);
		}
		
		return ResponseHelper.ok(opendataMeasurement);
	}
	
	/**
	 * Get details of an open-data measurement.
	 * Returns the measurement details, either in grouped or plain form.
	 *
	 * @param agentUuid
	 * @param uuid
	 * @return
	 */
	@io.swagger.annotations.ApiOperation(value = "Get details of an open-data measurement.", notes = "Returns the measurement details, either in grouped or plain form.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(value = "/{openDataUuid:[^\\\\.]+}/details", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<DetailMeasurementResponse>> getMeasurementDetails(@ApiParam(value = "The open-data UUID", required = true) @PathVariable String openDataUuid, Locale locale) {
		
		// TODO: hardcoded until we have settings in Elasticsearch
		final String groupSettings = "[\n" + 
		"    {\n" + 
		"      \"key\": \"group_overview\",\n" + 
		"      \"icon\": \"r\",\n" + 
		"      \"values\": [\n" + 
		"        {\n" + 
		"          \"key\": \"speed.rttInfo.averageNs\",\n" + 
		"          \"format\": \"DIV_1E6\",\n" + 
		"          \"unit\": \"ms\",\n" + 
		"          \"translation_key\": \"translate_ping\",\n" + 
		"          \"share_text\": \"share_ping {}\",\n" + 
		"          \"share_priority\": 3\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"speed.throughputAvgDownloadBps\",\n" + 
		"          \"unit\": \"Mbps\",\n" + 
		"          \"format\": \"DIV_1E6\",\n" + 
		"          \"translation_key\": \"translate_down\",\n" + 
		"          \"share_text\": \"share_download {}\",\n" + 
		"          \"share_priority\": 1\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"speed.throughputAvgUploadBps\",\n" + 
		"          \"unit\": \"Mbps\",\n" + 
		"          \"format\": \"DIV_1E6\",\n" + 
		"          \"translation_key\": \"translate_up\",\n" + 
		"          \"share_text\": \"share_upload {}\",\n" + 
		"          \"share_priority\": 2\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"openDataUuid\",\n" + 
		"          \"translation_key\": \"uuid\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"systemUuid\",\n" + 
		"          \"translation_key\": \"measurement_system_uuid\",\n" + 
		"          \"share_text\": \"share_system {}\",\n" + 
		"          \"share_priority\": 5\n" + 
		"        }\n" + 
		"      ]\n" + 
		"    },\n" + 
		"    {\n" + 
		"      \"key\": \"group_device\",\n" + 
		"      \"icon\": \"b\",\n" + 
		"      \"values\": [\n" + 
		"        {\n" + 
		"          \"key\": \"deviceInfo.osInfo.name\",\n" + 
		"          \"translation_key\": \"translate_osName\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"deviceInfo.osInfo.version\",\n" + 
		"          \"translation_key\": \"translate_version\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"deviceInfo.codeName\",\n" + 
		"          \"translation_key\": \"translate_codename\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"deviceInfo.model\",\n" + 
		"          \"translation_key\": \"translate_model\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"agentInfo.timezone\",\n" + 
		"          \"translation_key\": \"translate_timezone\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"geoLocationInfo.distanceMovedMetres\",\n" + 
		"          \"translation_key\": \"translate_distMetres\",\n" + 
		"          \"unit\": \"m\"\n" + 
		"        }\n" + 
		"      ]\n" + 
		"    },\n" + 
		"    {\n" + 
		"      \"key\": \"group_speed_connectivity\",\n" + 
		"      \"icon\": \"ú\",\n" + 
		"      \"values\": [\n" + 
		"        {\n" + 
		"          \"key\": \"speed.connectionInfo.address\",\n" + 
		"          \"translation_key\": \"address\",\n" + 
		"          \"share_text\": \"share_address {}\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"speed.connectionInfo.port\",\n" + 
		"          \"translation_key\": \"port\"\n" + 
		"        },\n" + 
		"        {\n" + 
		"          \"key\": \"speed.connectionInfo.encrypted\",\n" + 
		"          \"translation_key\": \"encryption\"\n" + 
		"        }\n" + 
		"      ]\n" + 
		"    }\n" + 
		"  ]";
		
		List<SpeedtestDetailGroup> groupStructure = null;
		
		try {
			groupStructure = objectMapper.readValue(groupSettings, new TypeReference<List<SpeedtestDetailGroup>>() {});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		final Map<String, Object> measurementAsMap = searchService.findOneByOpenDataUuid(openDataUuid);
		if (measurementAsMap == null) {
			throw new RuntimeException("Measurement with openDataUuid " + openDataUuid + " not found");
		}
		
		return ResponseHelper.ok(groupedMeasurementService.groupResult(measurementAsMap, groupStructure, locale, 10000)); // TODO: config
	}
}
