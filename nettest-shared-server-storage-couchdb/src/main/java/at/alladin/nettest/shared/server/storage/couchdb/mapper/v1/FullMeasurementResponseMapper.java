package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurement;

@Mapper(componentModel = "spring")
public interface FullMeasurementResponseMapper extends DateTimeMapper {

	@Mappings({
		@Mapping(source="measurementTime.startTime", target="startTime"),
		@Mapping(source="measurementTime.durationNs", target="durationNs"),
		@Mapping(source="measurementTime.endTime", target="endTime"),
		@Mapping(source="geoLocationInfo.geoLocations", target="geoLocations"),
		@Mapping(expression="java(parseSubMeasurementMap(measurement.getMeasurements()))", target="measurements"),
	})
	FullMeasurementResponse map(Measurement measurement);
	
	@Mappings({
		@Mapping(source="measurementTime.relativeStartTimeNs", target="relativeStartTimeNs"),
		@Mapping(source="measurementTime.relativeEndTimeNs", target="relativeEndTimeNs"),
		@Mapping(source="statusInfo.status", target="status"),
		@Mapping(source="statusInfo.reason", target="reason"),
	})
	FullQoSMeasurement map(QoSMeasurement measurement);
	
	@Mappings({
		@Mapping(source="measurementTime.relativeStartTimeNs", target="relativeStartTimeNs"),
		@Mapping(source="measurementTime.relativeEndTimeNs", target="relativeEndTimeNs"),
		@Mapping(source="statusInfo.status", target="status"),
		@Mapping(source="statusInfo.reason", target="reason"),
		@Mapping(source="speedRawData.download", target="downloadRawData"),
		@Mapping(source="speedRawData.upload", target="uploadRawData"),
	})
	FullSpeedMeasurement map(SpeedMeasurement measurement);
	
	default Map<MeasurementTypeDto, FullSubMeasurement> parseSubMeasurementMap(Map<MeasurementTypeDto, SubMeasurement> measurementParam) {
		final Map<MeasurementTypeDto, FullSubMeasurement> ret = new HashMap<>();
		for (MeasurementTypeDto type : measurementParam.keySet()) {
			switch (type) {
			case SPEED:
				ret.put(type, this.map((SpeedMeasurement) measurementParam.get(type)));
				break;
			case QOS:
				ret.put(type, this.map((QoSMeasurement) measurementParam.get(type)));
				break;
			}
			
		}
		return ret;
	}
	
}
