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
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters.SpeedMeasurementConfiguration;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters.SpeedMeasurementConfiguration.SpeedMeasurementClass;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MeasurementServer;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementObjective;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings;

@Mapper(componentModel = "spring")
public interface LmapTaskMapper {

	public final static String MEASUREMENT_PARAMETER_QOS = "parameters_qos";
	
	public final static String MEASUREMENT_PARAMETER_SPEED = "parameters_speed";
	
	public final static String QOS_NAME = "QOS";
	
	public final static String CONCURRENCY_GROUP = "concurrency_group";
	
	public final static String QOS_TEST_UID = "qos_test_uid";
	
	public final static String QOS_TEST_TYPE = "qostest";
	
	public final static String SERVER_ADDRESS = "server_addr";
	
	public final static String SERVER_ADDRESS_IPV6 = "server_addr_ipv6";
	
	public final static String SERVER_PORT = "server_port";
	
	public final static String ENCRYPTION = "encryption";
	
	public final static String RESULT_COLLECTOR_URL = "result_collector_base_url";
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionQoSList(settings, measurementServer, qosObjectiveList) )"
				),
		@Mapping(target="name", source="name")
	})
	LmapTaskDto map(Settings settings, MeasurementServer measurementServer, List<QoSMeasurementObjective> qosObjectiveList, String name);
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionSpeedList(settings) )"
				)
	})
	LmapTaskDto map(Settings settings, String name);
	
	@Mappings({
//		@Mapping(target="uploadClassList", source="uploadClassList"),
//		@Mapping(target="downloadClassList", source="downloadClassList")
	})
	SpeedMeasurementConfiguration map(SpeedMeasurementSettings speedSettings);
	
	List<SpeedMeasurementClass> map(List<at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings.SpeedMeasurementClass> speedMeasurementClass);
	
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
	
	default List<LmapOptionDto> buildOptionSpeedList(final Settings settings) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		if (settings != null && settings.getUrls() != null && settings.getUrls().getCollectorService() != null) {
			ret.add(generateOption(RESULT_COLLECTOR_URL, settings.getUrls().getCollectorService()));
		}
		if (settings.getMeasurements() != null) {
			final SpeedMeasurementSettings speedSettings = (SpeedMeasurementSettings) settings.getMeasurements().get(MeasurementTypeDto.SPEED);
			if (speedSettings != null) {
				final LmapOptionDto paramDto = new LmapOptionDto();
				final SpeedMeasurementTypeParameters parameters = new SpeedMeasurementTypeParameters();
				parameters.setMeasurementConfiguration(map(speedSettings));
				paramDto.setMeasurementParameters(parameters);
				paramDto.setName(MEASUREMENT_PARAMETER_SPEED);
				ret.add(paramDto);
			}
		}
		return ret;
	}
	
	default List<LmapOptionDto> buildOptionQoSList(final Settings settings, final MeasurementServer measurementServer, List<QoSMeasurementObjective> qosObjectiveList) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		if (settings != null && settings.getUrls() != null && settings.getUrls().getCollectorService() != null) {
			ret.add(generateOption(RESULT_COLLECTOR_URL, settings.getUrls().getCollectorService()));
		}
		
		if (measurementServer != null) {
			if (measurementServer.getAddressIpv4() != null) {
				ret.add(generateOption(SERVER_ADDRESS, measurementServer.getAddressIpv4()));
			}
			if (measurementServer.getAddressIpv6() != null) {
				ret.add(generateOption(SERVER_ADDRESS_IPV6, measurementServer.getAddressIpv6()));
			}
			if (measurementServer.getPort() != null) {
				ret.add(generateOption(SERVER_PORT, measurementServer.getPort().toString()));
			}
			
			
		}
		final LmapOptionDto measurementOption = new LmapOptionDto();
		if (qosObjectiveList != null) {
			measurementOption.setMeasurementParameters(createQoSParameters(qosObjectiveList));
		} 
		measurementOption.setName(MEASUREMENT_PARAMETER_QOS);
		ret.add(measurementOption);
		
		
		return ret;
	}
	
	default LmapOptionDto generateOption(final String name, final String value) {
		final LmapOptionDto option = new LmapOptionDto();
		option.setName(name);
		option.setValue(value);
		return option;
	}
	
}
