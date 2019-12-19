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

package at.alladin.nettest.service.collector.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultNetworkPointInTimeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.TimeBasedResultDto;

@Service
public class ResultPreProcessService {
	
	private static final Logger logger = LoggerFactory.getLogger(ResultPreProcessService.class);
	
	private final String[] HTTP_HEADER_IP_ADDRESS_FIELDS = {
		    "X-Real-IP",
		    "X-Client-IP",
			"X-FORWARDED-FOR"
		};

	public void addClientIpToReportDto(final LmapReportDto lmapReportDto, final HttpServletRequest request) {
		if (lmapReportDto != null) {
			if (lmapReportDto.getTimeBasedResult() == null) {
				lmapReportDto.setTimeBasedResult(new TimeBasedResultDto());
			}
			if (lmapReportDto.getTimeBasedResult().getNetworkPointsInTime() == null) {
				lmapReportDto.getTimeBasedResult().setNetworkPointsInTime(new ArrayList<>());
			}
			final List<MeasurementResultNetworkPointInTimeDto> networkPointsInTime = lmapReportDto.getTimeBasedResult().getNetworkPointsInTime();
			if (networkPointsInTime.size() == 0) {
				networkPointsInTime.add(new MeasurementResultNetworkPointInTimeDto());
			}
			final String clientPublicIp = getClientIpAddressString(request);
			for (MeasurementResultNetworkPointInTimeDto pointInTime : networkPointsInTime) {
				if (pointInTime != null && pointInTime.getClientPublicIp() == null) {
					pointInTime.setClientPublicIp(clientPublicIp);
				}
			}
		}
	}

	private String getClientIpAddressString(final HttpServletRequest request) {
		for (String field : HTTP_HEADER_IP_ADDRESS_FIELDS) { // TODO: add correct handling (order) of these values
			final String ipAddress = request.getHeader(field);
			
			if (StringUtils.hasLength(ipAddress) && !"127.0.0.1".equals(ipAddress) && !ipAddress.contains("%")) {
				logger.debug("Found ip address {} in {} header", ipAddress, field);
				return ipAddress;
			}
		}
		
		logger.debug("Did not get ip address from headers, using request.getRemoteAddr");
		
		// workaround for %1 after ipv6... TODO: fix this properly
		String ipAddress = request.getRemoteAddr();
		if (ipAddress.contains("%")) {
			ipAddress = ipAddress.substring(0, ipAddress.lastIndexOf('%'));
		}
		
		return ipAddress;

	}
}
