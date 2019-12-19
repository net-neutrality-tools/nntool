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

package at.alladin.nettest.service.search.helper;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;

public interface CoarseResultHelper {

	// TODO: rewrite using BriefMeasurementResponseMapper -> map from FullMeasurementResponse
	static Map<String, Object> makeCoarseResult(Map<String, Object> measurement, ObjectMapper objectMapper) {
		final FullMeasurementResponse mappedMeasurement = objectMapper.convertValue(measurement, FullMeasurementResponse.class);
		
		mappedMeasurement.setGeoLocations(null);
		
		if (mappedMeasurement.getMeasurements() != null) {
			mappedMeasurement.getMeasurements().remove(MeasurementTypeDto.QOS);
		}
		
		if (mappedMeasurement.getNetworkInfo() != null) {
			mappedMeasurement.getNetworkInfo().setNetworkPointInTimeInfo(null);
			mappedMeasurement.getNetworkInfo().setSignals(null);
		}
		
		final FullSubMeasurement iasSubMeasurement = mappedMeasurement.getMeasurements().get(MeasurementTypeDto.SPEED);
		if (iasSubMeasurement != null && iasSubMeasurement instanceof FullSpeedMeasurement) { 
			final FullSpeedMeasurement iasMeasurement = (FullSpeedMeasurement) iasSubMeasurement;
			
			iasMeasurement.setConnectionInfo(null);
			iasMeasurement.setDownloadRawData(null);
			iasMeasurement.setUploadRawData(null);
			
			if (iasMeasurement.getRttInfo() != null) {
				iasMeasurement.getRttInfo().setRtts(null);
			}
		}
		
		final ObjectMapper nonNullObjectMapper = objectMapper.copy().setSerializationInclusion(Include.NON_NULL);
		
		return nonNullObjectMapper.convertValue(mappedMeasurement, new TypeReference<Map<String, Object>>() {});
	}
}
