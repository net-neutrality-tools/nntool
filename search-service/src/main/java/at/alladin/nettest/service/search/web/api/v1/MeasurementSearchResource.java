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

import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.search.helper.CoarseResultHelper;
import at.alladin.nettest.service.search.service.SearchService;
import at.alladin.nettest.service.search.service.SettingsService;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.detail.DetailMeasurementResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;
import at.alladin.nettest.shared.server.helper.swagger.ApiPageable;
import at.alladin.nettest.shared.server.model.ServerSettings;
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
	private SettingsService settingsService;
	
	@Autowired
	private GroupedMeasurementService groupedMeasurementService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
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
		final ServerSettings settings = settingsService.getSettings();
		List<SpeedtestDetailGroup> groupStructure = settings.getSpeedtestDetailGroups();
		
		final Map<String, Object> measurementAsMap = searchService.findOneByOpenDataUuid(openDataUuid);
		if (measurementAsMap == null) {
			throw new RuntimeException("Measurement with openDataUuid " + openDataUuid + " not found");
		}
		
		return ResponseHelper.ok(groupedMeasurementService.groupResult(measurementAsMap, groupStructure, locale, 10000)); // TODO: config
	}
}
