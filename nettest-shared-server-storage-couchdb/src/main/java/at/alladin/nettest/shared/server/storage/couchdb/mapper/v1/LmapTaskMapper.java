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
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings.SpeedMeasurementClassConfiguration;
import at.alladin.nntool.shared.qos.util.SipTaskHelper;

@Mapper(componentModel = "spring")
public interface LmapTaskMapper {
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionQoSList(settings, measurementServer, qosObjectiveList, useIPv6, browserName) )"
				),
		@Mapping(target="name", source="name")
	})
	LmapTaskDto map(Settings settings, MeasurementServer measurementServer, List<QoSMeasurementObjective> qosObjectiveList, String name, boolean useIPv6, String browserName);
	
	@Mappings({
		@Mapping(target="options",
				expression= "java( buildOptionSpeedList(settings, measurementServer, useIPv6, browserName) )"
				),
		@Mapping(target="name", source="name")
	})
	LmapTaskDto map(Settings settings, MeasurementServer measurementServer, String name, boolean useIPv6, String browserName);
	
	List<SpeedMeasurementClass> map(List<at.alladin.nettest.shared.server.storage.couchdb.domain.model.Settings.SpeedMeasurementSettings.SpeedMeasurementClass> speedMeasurementClass);
	
	default QoSMeasurementTypeParameters createQoSParameters(List<QoSMeasurementObjective> qosObjectiveList) {
		final QoSMeasurementTypeParameters ret = new QoSMeasurementTypeParameters();
		for (QoSMeasurementObjective objective : qosObjectiveList)  {
			if (objective.getType() == null) {
				continue;
			}
			final QoSMeasurementTypeDto dtoType;
			try {
				dtoType = QoSMeasurementTypeDto.valueOf(objective.getType().toString());
			} catch (Exception ex) {
				continue;
			}
			if (!ret.getObjectives().containsKey(dtoType)) {
				ret.getObjectives().put(dtoType, new ArrayList<>());
			}
			final Map<String, Object> objectiveMap = new HashMap<>();
			objectiveMap.put(LmapTaskDto.CONCURRENCY_GROUP, objective.getConcurrencyGroup());
			objectiveMap.put(LmapTaskDto.QOS_TEST_UID, objective.getObjectiveId());
			objectiveMap.put(LmapTaskDto.QOS_TEST_TYPE, objective.getType().toString());
			objective.getParameters().forEach((key, value) -> objectiveMap.put(key, value));
			ret.getObjectives().get(dtoType).add(objectiveMap);
			
			if (QoSMeasurementType.SIP.equals(objective.getType())) {
				SipTaskHelper.preProcess(objectiveMap);
			}
		}
		
		return ret;
	}
	
	default List<LmapOptionDto> buildOptionSpeedList(final Settings settings, final MeasurementServer measurementServer, boolean useIPv6, String browserName) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		if (settings != null && settings.getUrls() != null && settings.getUrls().getCollectorService() != null) {
			ret.add(generateOption(LmapTaskDto.RESULT_COLLECTOR_URL, settings.getUrls().getCollectorService()));
		}
		
		addMeasurementServerOptionsToList(ret, measurementServer, useIPv6);
		
		if (settings.getMeasurements() != null) {
			final SpeedMeasurementSettings speedSettings = (SpeedMeasurementSettings) settings.getMeasurements().get(MeasurementTypeDto.SPEED);
			if (speedSettings != null) {
				final LmapOptionDto paramDto = new LmapOptionDto();
				final SpeedMeasurementTypeParameters parameters = new SpeedMeasurementTypeParameters();
				parameters.setMeasurementConfiguration(createSpeedMeasurementConfiguration(speedSettings, browserName));
				paramDto.setMeasurementParameters(parameters);
				paramDto.setName(LmapTaskDto.MEASUREMENT_PARAMETER_SPEED);
				ret.add(paramDto);
			}
		}

		return ret;
	}
	
	default SpeedMeasurementConfiguration createSpeedMeasurementConfiguration(SpeedMeasurementSettings speedSettings, String browserName) {
		final SpeedMeasurementConfiguration conf = new SpeedMeasurementConfiguration();
		
		final Map<String, SpeedMeasurementClassConfiguration> confByUserAgent = speedSettings.getClassesByUserUserAgent();
		if (confByUserAgent != null) {
			SpeedMeasurementClassConfiguration browserConf = confByUserAgent.get(browserName);
			if (browserConf != null && !browserConf.getDownloadClassList().isEmpty() && !browserConf.getUploadClassList().isEmpty()) {
				conf.setDownloadClassList(map(browserConf.getDownloadClassList()));
				conf.setUploadClassList(map(browserConf.getUploadClassList()));
		
				return conf;
			}
			
			// if not found, try only browser without operating system
			if (browserName.contains(".")) {
				browserConf = confByUserAgent.get(browserName.split(".")[1]);
				
				if (browserConf != null && !browserConf.getDownloadClassList().isEmpty() && !browserConf.getUploadClassList().isEmpty()) {
					conf.setDownloadClassList(map(browserConf.getDownloadClassList()));
					conf.setUploadClassList(map(browserConf.getUploadClassList()));
				
					return conf;
				}
			}
		}

		// Fallback: if no config for browserName then return default config.
		final SpeedMeasurementClassConfiguration defaultClasses = speedSettings.getDefaultClasses();
		if (defaultClasses != null) {
			conf.setDownloadClassList(map(speedSettings.getDefaultClasses().getDownloadClassList()));
			conf.setUploadClassList(map(speedSettings.getDefaultClasses().getUploadClassList()));
		}
		
		return conf;
	}
	
	default List<LmapOptionDto> buildOptionQoSList(final Settings settings, final MeasurementServer measurementServer, List<QoSMeasurementObjective> qosObjectiveList, boolean useIPv6, String browserName) {
		final List<LmapOptionDto> ret = new ArrayList<>();
		
		if (settings != null && settings.getUrls() != null && settings.getUrls().getCollectorService() != null) {
			ret.add(generateOption(LmapTaskDto.RESULT_COLLECTOR_URL, settings.getUrls().getCollectorService()));
		}
		
		addMeasurementServerOptionsToList(ret, measurementServer, useIPv6);
		
		final LmapOptionDto measurementOption = new LmapOptionDto();
		if (qosObjectiveList != null) {
			measurementOption.setMeasurementParameters(createQoSParameters(qosObjectiveList));
		} 
		measurementOption.setName(LmapTaskDto.MEASUREMENT_PARAMETER_QOS);
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
                optionList.add(generateOption(LmapTaskDto.SERVER_ADDRESS, measurementServer.getAddressIpv4()));
            }

            if (measurementServer.getAddressIpv6() != null) {
                optionList.add(generateOption(LmapTaskDto.SERVER_ADDRESS_IPV6, measurementServer.getAddressIpv6()));
            }

            if (useIPv6) {
                optionList.add(generateOption(LmapTaskDto.SERVER_ADDRESS_DEFAULT, measurementServer.getAddressIpv6()));
            } else {
                optionList.add(generateOption(LmapTaskDto.SERVER_ADDRESS_DEFAULT, measurementServer.getAddressIpv4()));
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

            optionList.add(generateOption(LmapTaskDto.SERVER_PORT, String.valueOf(port)));
            optionList.add(generateOption(LmapTaskDto.ENCRYPTION, String.valueOf(encryption)));
        }
	}
	
}
