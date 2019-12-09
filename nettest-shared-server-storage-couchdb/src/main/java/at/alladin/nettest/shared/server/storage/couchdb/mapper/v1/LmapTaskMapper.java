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
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings;
import at.alladin.nntool.shared.qos.util.SipTaskHelper;

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
	
	public final static String SERVER_ADDRESS_DEFAULT = "server_addr_default"; // should be only "server_addr"
	
	public final static String SERVER_PORT = "server_port";
	
	public final static String ENCRYPTION = "encryption";
	
	public final static String RESULT_COLLECTOR_URL = "result_collector_base_url";
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionQoSList(settings, measurementServer, qosObjectiveList, useIPv6) )"
				),
		@Mapping(target="name", source="name")
	})
	LmapTaskDto map(Settings settings, MeasurementServer measurementServer, List<QoSMeasurementObjective> qosObjectiveList, String name, boolean useIPv6);
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionSpeedList(settings, measurementServer, useIPv6) )"
				),
		@Mapping(target="name", source="name")
	})
	LmapTaskDto map(Settings settings, MeasurementServer measurementServer, String name, boolean useIPv6);
	
	@Mappings({
//		@Mapping(target="uploadClassList", source="uploadClassList"),
//		@Mapping(target="downloadClassList", source="downloadClassList")
	})
	SpeedMeasurementConfiguration map(SpeedMeasurementSettings speedSettings);
	
	List<SpeedMeasurementClass> map(List<at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings.SpeedMeasurementClass> speedMeasurementClass);
	
	default QoSMeasurementTypeParameters createQoSParameters(List<QoSMeasurementObjective> qosObjectiveList) {
		final QoSMeasurementTypeParameters ret = new QoSMeasurementTypeParameters();
		for (QoSMeasurementObjective objective : qosObjectiveList)  {
			if (objective.getType() == null) {
				continue;
			}
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
			
			if (QoSMeasurementType.SIP.equals(objective.getType())) {
				SipTaskHelper.preProcess(objectiveMap);
			}
			
		}
		
		return ret;
	}
	
	default List<LmapOptionDto> buildOptionSpeedList(final Settings settings, final MeasurementServer measurementServer, boolean useIPv6) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		if (settings != null && settings.getUrls() != null && settings.getUrls().getCollectorService() != null) {
			ret.add(generateOption(RESULT_COLLECTOR_URL, settings.getUrls().getCollectorService()));
		}
		
		addMeasurementServerOptionsToList(ret, measurementServer, useIPv6);
		
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
	
	default List<LmapOptionDto> buildOptionQoSList(final Settings settings, final MeasurementServer measurementServer, List<QoSMeasurementObjective> qosObjectiveList, boolean useIPv6) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		if (settings != null && settings.getUrls() != null && settings.getUrls().getCollectorService() != null) {
			ret.add(generateOption(RESULT_COLLECTOR_URL, settings.getUrls().getCollectorService()));
		}
		
		addMeasurementServerOptionsToList(ret, measurementServer, useIPv6);
		
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
	
	default void addMeasurementServerOptionsToList(final List<LmapOptionDto> optionList, final MeasurementServer measurementServer, boolean useIPv6) {
		if (measurementServer != null) {
			
			////
			// host ipv4/6
			
			if (measurementServer.getAddressIpv4() != null) {
				optionList.add(generateOption(SERVER_ADDRESS, measurementServer.getAddressIpv4()));
			}
			
			if (measurementServer.getAddressIpv6() != null) {
				optionList.add(generateOption(SERVER_ADDRESS_IPV6, measurementServer.getAddressIpv6()));
			}
			
			if (useIPv6) {
				optionList.add(generateOption(SERVER_ADDRESS_DEFAULT, measurementServer.getAddressIpv6()));
			} else {
				optionList.add(generateOption(SERVER_ADDRESS_DEFAULT, measurementServer.getAddressIpv4()));
			}
			
			////
			// port and encryption
			
			Integer port = null;
			boolean encryption = false;
			
			if (measurementServer.isPreferEncryption()) {
				port = measurementServer.getPortTls();
				encryption = true;
			}
			
			// Encryption is not preferred or no TLS port is configured -> fall back to non-encrypted port.
			if (port == null) {
				port = measurementServer.getPort();
				encryption = false;
			}
			
			optionList.add(generateOption(SERVER_PORT, String.valueOf(port)));
			optionList.add(generateOption(ENCRYPTION, String.valueOf(encryption)));
		}
	}
	
}
