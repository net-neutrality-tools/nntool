package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;

@Mapper(componentModel = "spring")
public interface FullMeasurementResponseMapper extends DateTimeMapper {

	@Mappings({
		@Mapping(source="measurementTime.startTime", target="startTime"),
		@Mapping(source="measurementTime.durationNs", target="durationNs"),
		@Mapping(source="measurementTime.endTime", target="endTime"),
		@Mapping(source="geoLocationInfo.geoLocations", target="geoLocations")
	})
	FullMeasurementResponse map(Measurement measurement);
	
}
