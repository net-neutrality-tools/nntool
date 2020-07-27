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

package at.alladin.nettest.service.controller.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.alladin.nettest.service.controller.config.ControllerServiceProperties;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapActionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapEventDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapImmediateEventDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapScheduleDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapScheduleDto.ExecutionMode;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapStopDurationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.berec.loadbalancer.api.v1.dto.MeasurementServerDto;
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;
import at.alladin.nntool.shared.qos.QosMeasurementType;

@Service
public class MeasurementConfigurationService {
	
	private static final Logger logger = LoggerFactory.getLogger(MeasurementConfigurationService.class);
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ControllerServiceProperties controllerServiceProperties;

    @Autowired(required = false)
    private LoadBalancingService loadBalancingService;

    public LmapControlDto getLmapControlDtoForCapabilities(final LmapCapabilityDto capabilities, boolean useIPv6, String browserName) {
		final LmapControlDto ret = new LmapControlDto();
		
		ret.setCapabilities(capabilities);
		ret.setTasks(getTaskListForCapabilities(capabilities, useIPv6, browserName));
		ret.setEvents(getImmediateEventList());
		ret.setSchedules(getLmapScheduleList(ret.getEvents().get(0).getName(), ret.getTasks()));

		String preferredServerId = null;
		for (LmapCapabilityTaskDto task : capabilities.getTasks()) {
			switch(MeasurementTypeDto.valueOf(task.getTaskName().toUpperCase())) {
				case SPEED:
					preferredServerId = task.getSelectedMeasurementPeerIdentifier();
					break;
				default:
					break;
			}
		};
		
		if (loadBalancingService != null) {
			MeasurementServerDto serverDto = loadBalancingService.getNextAvailableMeasurementServer(controllerServiceProperties.getSettingsUuid(), preferredServerId);
			logger.debug("Got measurement peer from load balancer: {}", serverDto);
			if (serverDto != null && ret.getTasks() != null) {
	            Integer port = null;
	            boolean encryption = false;

	            if (serverDto.isPreferEncryption()) {
	                port = serverDto.getPortTls();
	                encryption = true;
	            }

	            if (port == null) {
	                port = serverDto.getPort();
	                encryption = false;
	            }
	            
				for (final LmapTaskDto task : ret.getTasks()) {
					if (task.getName() != null && task.getOptions() != null) {
						final MeasurementTypeDto type = MeasurementTypeDto.valueOf(task.getName().toUpperCase());
						if (type == MeasurementTypeDto.QOS) {
		                    for (final LmapOptionDto option : task.getOptions()) {
		                        if (option.getMeasurementParameters() != null) {
		                            final QoSMeasurementTypeParameters qosParams = (QoSMeasurementTypeParameters) option.getMeasurementParameters();
		                            for (final Map.Entry<QoSMeasurementTypeDto, List<Map<String, Object>>> e : qosParams.getObjectives().entrySet()) {
		                            	for (final Map<String, Object> qosParamMap : e.getValue()) {
			                                try {
			                                    final QosMeasurementType qosType = QosMeasurementType.fromQosTypeDto(e.getKey());
			                                    switch(qosType) {
				                                    case TRACEROUTE:
				                                    	final Boolean isReverse = Boolean.valueOf(String.valueOf(qosParamMap.get("is_reverse")));
				                                    	if (isReverse) {
				                                    		qosParamMap.put("auth_token", serverDto.getAuthToken());
				                                    		qosParamMap.put("auth_timestamp", serverDto.getAuthTimestamp());
				                                    		qosParamMap.put("host", serverDto.getTracerouteUrl());
				                                    	}
				                                    	break;
				                                    default:
				                                    	break;
			                                    }
			                                } catch (final Exception ex) {
			                                    //unknown qos type
			                                    ex.printStackTrace();
			                                }
		                            	}
		                            }
		                        }
		                    }
						}
						else if (type == MeasurementTypeDto.SPEED) {
							if (serverDto.getAuthToken() != null && serverDto.getAuthTimestamp() != null) {
								final LmapOptionDto authTokenOption = new LmapOptionDto();
								authTokenOption.setName(LmapTaskDto.AUTH_TOKEN);
								authTokenOption.setValue(serverDto.getAuthToken());
								task.getOptions().add(authTokenOption);

								final LmapOptionDto authTsOption = new LmapOptionDto();
								authTsOption.setName(LmapTaskDto.AUTH_TIMESTAMP);
								authTsOption.setValue(String.valueOf(serverDto.getAuthTimestamp()));
								task.getOptions().add(authTsOption);
							}

							for (final LmapOptionDto option : task.getOptions()) {
								switch(option.getName()) {
								case LmapTaskDto.SERVER_ADDRESS:
									option.setValue(serverDto.getAddressIpv4());
									break;
								case LmapTaskDto.SERVER_ADDRESS_IPV6:
									option.setValue(serverDto.getAddressIpv6());
									break;
								case LmapTaskDto.SERVER_ADDRESS_DEFAULT:
									option.setValue(useIPv6 && serverDto.getAddressIpv6() != null ? serverDto.getAddressIpv6() : serverDto.getAddressIpv4());
									break;
								case LmapTaskDto.SERVER_PORT:
									option.setValue(String.valueOf(port));
									break;
								case LmapTaskDto.ENCRYPTION:
									option.setValue(String.valueOf(encryption));
									break;
								}
							}
						}
					}
				}
			}
		}

		try {
			logger.debug("{}", new ObjectMapper().writeValueAsString(ret));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * Fetches the matching task configurations for the given capabilities.
	 * 
	 * @param capabilities
	 * @return
	 */
	private List<LmapTaskDto> getTaskListForCapabilities(final LmapCapabilityDto capabilities, boolean useIPv6, String browserName) {
		final List<LmapTaskDto> ret = new ArrayList<>();
		
		for (LmapCapabilityTaskDto capability : capabilities.getTasks()) {
			final MeasurementTypeDto type;
			try {
				type = MeasurementTypeDto.valueOf(capability.getTaskName().toUpperCase());
			} catch(IllegalArgumentException ex) {
				logger.error(String.format("Unknown measurement type of name %s requested. Ignoring.", capability.getTaskName()));
				continue;
			}
			LmapTaskDto task = getMeasurementTaskConfiguration(type, capability, useIPv6, browserName);
			if (task != null) {
				ret.add(task);
			}
		}
		
		return ret;
	}
	
	private List<LmapEventDto> getImmediateEventList () {
		final List<LmapEventDto> ret = new ArrayList<>();
		final LmapEventDto immediateEvent = new LmapEventDto();
		immediateEvent.setEvent(new LmapImmediateEventDto());
		immediateEvent.setName("immediate_event");
		ret.add(immediateEvent);
		return ret;
	}
	
	private List<LmapScheduleDto> getLmapScheduleList(final String eventName, final List<LmapTaskDto> taskList) {
		final List<LmapScheduleDto> ret = new ArrayList<>();
		
		final LmapScheduleDto schedule = new LmapScheduleDto();
		ret.add(schedule);
		
		final List<LmapActionDto> actionList = new ArrayList<>();
		schedule.setActions(actionList);
		
		schedule.setExecutionMode(ExecutionMode.SEQUENTIAL);
		schedule.setName("immediate_schedule");
		schedule.setStart(eventName);
		LmapStopDurationDto stop = new LmapStopDurationDto();
		stop.setDuration(180000); // TODO: read from DB
		schedule.setStop(stop);
		
		for (LmapTaskDto task : taskList) {
			final LmapActionDto action = new LmapActionDto();
			action.setName(task.getName() + "_action");
			action.setTaskName(task.getName());
			actionList.add(action);
		}
		
		
		return ret;
	}
	
	private LmapTaskDto getMeasurementTaskConfiguration(final MeasurementTypeDto name, final LmapCapabilityTaskDto capability, boolean useIPv6, String browserName) {
		if (name == null || capability == null) {
			return null;
		}

		final LmapTaskDto ret = storageService.getTaskDto(name, capability, controllerServiceProperties.getSettingsUuid(), useIPv6, browserName);
		final List<String> tagList = new ArrayList<String>();
		//tagList.add(version);
		
		if (ret != null) {
			ret.setTagList(tagList);
		}

		return ret;
	}
}
