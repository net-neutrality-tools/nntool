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

package at.alladin.nettest.service.statistic.web.api.v1;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.alladin.nettest.service.statistic.service.FilterService;
import at.alladin.nettest.service.statistic.service.ProviderStatisticsService;
import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderFilterResponseDto;
import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderStatisticDto;
import at.alladin.nettest.service.statistic.web.api.v1.dto.ProviderStatisticsRequestParams;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiPagination;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiResponse;
import at.alladin.nettest.shared.server.helper.ResponseHelper;

/**
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@RestController
@RequestMapping("/api/v1/statistics/providers")
public class ProviderStatisticResource {

	@Autowired
	private FilterService filterService;
	
	@Autowired
	private ProviderStatisticsService providerStatisticsService;

	@io.swagger.annotations.ApiOperation(value = "Retrieve the default list of filter elements for the providers statistics if no request parameters are provided, or rerieve an updated list of filter elements if one or more selected filter parameters are provided.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(path="/filters", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<ProviderFilterResponseDto>> getFilters(ProviderStatisticsRequestParams params, Locale locale) {
		final ProviderFilterResponseDto response = new ProviderFilterResponseDto();
		response.setFilters(filterService.getFiltersForProviderStatistics(null, locale));
		return ResponseHelper.ok(response);
	}
	
	@io.swagger.annotations.ApiOperation(value = "Retrieve the provider statistics filtered by the provided params.")
	@io.swagger.annotations.ApiResponses({
		@io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request", response = ApiResponse.class),
		@io.swagger.annotations.ApiResponse(code = 500, message = "Internal Server Error", response = ApiResponse.class)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ApiResponse<ApiPagination<ProviderStatisticDto>>> getProviderStatistics(
			ProviderStatisticsRequestParams params, Pageable page) {
		return ResponseHelper.ok(providerStatisticsService.getProviderStatistics(params, page));
	}
}
