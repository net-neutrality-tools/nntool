package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfiguration;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.TaskConfiguration.Option;

@Mapper(componentModel = "spring")
public interface LmapTaskMapper {

	public final static String MEASUREMENT_PARAMETER_PREFIX = "parameters_";
	
	public final static String QOS_NAME = "QOS";
	
	public final static String CONCURRENCY_GROUP = "concurrency_group";
	
	public final static String QOS_TEST_UID = "qos_test_uid";
	
	public final static String QOS_TEST_TYPE = "qostest";
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionList(taskConfiguration, qosObjectiveList) )"
				)
	})
	LmapTaskDto map(TaskConfiguration taskConfiguration, List<QoSMeasurementObjective> qosObjectiveList);
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionList(taskConfiguration) )"
				)
	})
	LmapTaskDto map(TaskConfiguration taskConfiguration);
	
	LmapOptionDto map(Option option);
		
	default QoSMeasurementTypeParameters createQoSParameters(List<QoSMeasurementObjective> qosObjectiveList) {
		final QoSMeasurementTypeParameters ret = new QoSMeasurementTypeParameters();
		for (QoSMeasurementObjective objective : qosObjectiveList)  {
			final QoSMeasurementTypeDto dtoType = QoSMeasurementTypeDto.valueOf(objective.getType().toString());
			if (!ret.getObjectives().containsKey(dtoType)) {
				ret.getObjectives().put(dtoType, new ArrayList<>());
			}
			final Map<String, Object> objectiveMap = new HashMap<>();
			objectiveMap.put(CONCURRENCY_GROUP, objective.getConcurrencyGroup());
			objectiveMap.put(QOS_TEST_UID, objective.getObjectiveId());
			objectiveMap.put(QOS_TEST_TYPE, objective.getType().toString());
			objective.getParameters().forEach((key, value) -> objectiveMap.put(key, value));
			ret.getObjectives().get(dtoType).add(objectiveMap);
		}
		
		return ret;
	}
	
	default List<LmapOptionDto> buildOptionList(final TaskConfiguration taskConfiguration) {
		return this.buildOptionList(taskConfiguration, null);
	}
	
	default List<LmapOptionDto> buildOptionList(final TaskConfiguration taskConfiguration, List<QoSMeasurementObjective> qosObjectiveList) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		for (Option option : taskConfiguration.getOptionList()) {
			ret.add(this.map(option));
		}
		
		final LmapOptionDto measurementOption = new LmapOptionDto();
		if (qosObjectiveList != null) {
			measurementOption.setMeasurementParameters(createQoSParameters(qosObjectiveList));
		} else {
			measurementOption.setMeasurementParameters(taskConfiguration.getMeasurementParameters());
		}
		measurementOption.setName(MEASUREMENT_PARAMETER_PREFIX + taskConfiguration.getName().toLowerCase());
		ret.add(measurementOption);
		
		
		return ret;
	}
	
}
