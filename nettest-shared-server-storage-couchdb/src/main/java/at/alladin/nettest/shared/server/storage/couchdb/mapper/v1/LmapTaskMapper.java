package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.agent.registration.RegistrationRequest;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementAgent;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfiguration;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfiguration.Option;

@Mapper(componentModel = "spring")
public interface LmapTaskMapper {

	public final static String MEASUREMENT_PARAMETER_PREFIX = "parameters_";
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionList(taskConfiguration) )"
				)
	})
	LmapTaskDto map(TaskConfiguration taskConfiguration);
	
	LmapOptionDto map(Option option);
	
	default List<LmapOptionDto> buildOptionList(final TaskConfiguration taskConfiguration) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		for (Option option : taskConfiguration.getOptionList()) {
			ret.add(this.map(option));
		}
		
		final LmapOptionDto measurementOption = new LmapOptionDto();
		measurementOption.setMeasurementParameters(taskConfiguration.getMeasurementParameters());
		measurementOption.setName(MEASUREMENT_PARAMETER_PREFIX + taskConfiguration.getName().toLowerCase());
		ret.add(measurementOption);
		
		return ret;
	}
	
}
