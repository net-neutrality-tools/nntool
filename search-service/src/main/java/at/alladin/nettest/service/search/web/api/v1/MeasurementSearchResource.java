/*******************************************************************************
 * Copyright 2019 alladin-IT GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.search.helper.CoarseResultHelper;
import at.alladin.nettest.service.search.service.SearchService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
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
				"      \"icon\": \"b\",\n" + 
				"      \"values\": [\n" + 
				"        {\n" + 
				"          \"key\": \"speed.throughputAvgDownloadBps\",\n" + 
				"          \"unit\": \"RESULT_WIFI_LINK_SPEED_UNIT\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"translation_key\": \"RESULT_DOWNLOAD\",\n" + 
				"          \"share_text\": \"RESULT_SHARE_DOWNLOAD\",\n" + 
				"          \"share_priority\": 1\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.throughputAvgUploadBps\",\n" + 
				"          \"unit\": \"RESULT_WIFI_LINK_SPEED_UNIT\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"translation_key\": \"RESULT_UPLOAD\",\n" + 
				"          \"share_text\": \"RESULT_SHARE_UPLOAD\",\n" + 
				"          \"share_priority\": 2\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.rttInfo.averageNs\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_MS_UNIT\",\n" + 
				"          \"translation_key\": \"RESULT_PING\",\n" + 
				"          \"share_text\": \"RESULT_SHARE_PING\",\n" + 
				"          \"share_priority\": 3\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"openDataUuid\",\n" + 
				"          \"translation_key\": \"key_open_test_uuid\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"systemUuid\",\n" + 
				"          \"translation_key\": \"result.system_uuid\",\n" + 
				"          \"share_text\": \"RESULT_SHARE_SYSTEM_UUID\",\n" + 
				"          \"share_priority\": 5\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"key\": \"group_network\",\n" + 
				"      \"icon\": \"d\",\n" + 
				"      \"values\": [\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.agentPublicIp\",\n" + 
				"          \"translation_key\": \"key_client_public_ip\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.agentPrivateIp\",\n" + 
				"          \"translation_key\": \"key_client_local_ip\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.agentPublicIpCountryCode\",\n" + 
				"          \"translation_key\": \"key_country_geoip\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.networkTypeName\",\n" + 
				"          \"translation_key\": \"RESULT_NETWORK_TYPE\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.networkTypeGroupName\",\n" + 
				"          \"translation_key\": \"result.network.network_group_name\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.providerShortName\",\n" + 
				"          \"translation_key\": \"key_provider\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.ssid\",\n" + 
				"          \"translation_key\": \"key_wifi_ssid\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.bssid\",\n" + 
				"          \"translation_key\": \"key_wifi_bssid\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.networkOperatorMccMnc\",\n" + 
				"          \"translation_key\": \"result.network.provider_mcc_mnc\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.networkOperatorName\",\n" + 
				"          \"translation_key\": \"key_network_operator_name\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"networkInfo.networkPointInTimeInfo.simOperatorName\",\n" + 
				"          \"translation_key\": \"key_network_sim_operator_name\"\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"key\": \"group_device\",\n" + 
				"      \"icon\": \"g\",\n" + 
				"      \"values\": [\n" + 
				"        {\n" + 
				"          \"key\": \"deviceInfo.osInfo.name\",\n" + 
				"          \"translation_key\": \"result.os\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"deviceInfo.osInfo.version\",\n" + 
				"          \"translation_key\": \"key_os_version\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"deviceInfo.codeName\",\n" + 
				"          \"translation_key\": \"result.device.codename\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"deviceInfo.model\",\n" + 
				"          \"translation_key\": \"key_model\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"agentInfo.timezone\",\n" + 
				"          \"translation_key\": \"key_timezone\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"geoLocationInfo.distanceMovedMetres\",\n" + 
				"          \"translation_key\": \"result.moved_distance\",\n" + 
				"          \"unit\": \"result.moved_distance.unit\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"agentInfo.appVersionName\",\n" + 
				"          \"translation_key\": \"key_client_software_version\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"agentInfo.timezone\",\n" + 
				"          \"translation_key\": \"key_timezone\"\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"key\": \"speed_parameter_group\",\n" + 
				"      \"icon\": \"B\",\n" + 
				"      \"values\": [\n" + 
				"        {\n" + 
				"          \"key\": \"speed.throughputAvgDownloadBps\",\n" + 
				"          \"unit\": \"RESULT_WIFI_LINK_SPEED_UNIT\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"translation_key\": \"RESULT_DOWNLOAD\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.bytesDownload\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_TOTAL_BYTES_UNIT\",\n" + 
				"          \"translation_key\": \"key_speed_download_bytes\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.durationDownloadNs\",\n" + 
				"          \"unit\": \"RESULT_DURATION_UNIT\",\n" + 
				"          \"format\": \"DIV_1E9\",\n" + 
				"          \"translation_key\": \"key_duration_dl\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.throughputAvgUploadBps\",\n" + 
				"          \"unit\": \"RESULT_WIFI_LINK_SPEED_UNIT\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"translation_key\": \"RESULT_UPLOAD\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.bytesUpload\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_TOTAL_BYTES_UNIT\",\n" + 
				"          \"translation_key\": \"key_speed_upload_bytes\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.durationUploadNs\",\n" + 
				"          \"unit\": \"RESULT_DURATION_UNIT\",\n" + 
				"          \"format\": \"DIV_1E9\",\n" + 
				"          \"translation_key\": \"key_duration_ul\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.rttInfo.averageNs\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_MS_UNIT\",\n" + 
				"          \"translation_key\": \"key_ping_average\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.rttInfo.medianNs\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_MS_UNIT\",\n" + 
				"          \"translation_key\": \"key_ping_median\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.rttInfo.minNs\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_MS_UNIT\",\n" + 
				"          \"translation_key\": \"key_ping_min\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.rttInfo.maxNs\",\n" + 
				"          \"format\": \"DIV_1E6\",\n" + 
				"          \"unit\": \"RESULT_MS_UNIT\",\n" + 
				"          \"translation_key\": \"key_ping_max\"\n" + 
				"        }\n" + 
				"      ]\n" + 
				"    },\n" + 
				"    {\n" + 
				"      \"key\": \"group_connectivity\",\n" + 
				"      \"icon\": \"q\",\n" + 
				"      \"values\": [\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.address\",\n" + 
				"          \"translation_key\": \"key_server_address\",\n" + 
				"          \"share_text\": \"RESULT_SHARE_MEASUREMENT_SERVER_ADDRESS\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.ipAddress\",\n" + 
				"          \"translation_key\": \"key_server_ip\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.port\",\n" + 
				"          \"translation_key\": \"key_port\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.encrypted\",\n" + 
				"          \"translation_key\": \"key_encryption\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.agentInterfaceTotalTraffic.bytesRx\",\n" + 
				"          \"translation_key\": \"result.connection.total_bytes_received\",\n" + 
				"          \"unit\": \"RESULT_TOTAL_BYTES_UNIT\",\n" + 
				"          \"format\": \"DIV_1E6\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.agentInterfaceTotalTraffic.bytesTx\",\n" + 
				"          \"translation_key\": \"result.connection.total_bytes_transmitted\",\n" + 
				"          \"unit\": \"RESULT_TOTAL_BYTES_UNIT\",\n" + 
				"          \"format\": \"DIV_1E6\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.actualNumStreamsDownload\",\n" + 
				"          \"translation_key\": \"key_num_threads\"\n" + 
				"        },\n" + 
				"        {\n" + 
				"          \"key\": \"speed.connectionInfo.actualNumStreamsUpload\",\n" + 
				"          \"translation_key\": \"key_num_threads_ul\"\n" + 
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
