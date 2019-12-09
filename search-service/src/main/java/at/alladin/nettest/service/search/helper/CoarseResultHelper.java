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
