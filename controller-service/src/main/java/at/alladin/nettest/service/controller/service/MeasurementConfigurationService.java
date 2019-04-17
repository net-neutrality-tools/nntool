package at.alladin.nettest.service.controller.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import at.alladin.nettest.shared.server.service.storage.v1.StorageService;

@Service
public class MeasurementConfigurationService {
	
	private static final Logger logger = LoggerFactory.getLogger(MeasurementConfigurationService.class);
	
	@Autowired
	private StorageService storageService;
	
	public LmapControlDto getLmapControlDtoForCapabilities(final LmapCapabilityDto capabilities) {
		final LmapControlDto ret = new LmapControlDto();
		
		ret.setCapabilities(capabilities);
		ret.setTasks(getTaskListForCapabilities(capabilities));
		ret.setEvents(getImmediateEventList());
		ret.setSchedules(getLmapScheduleList(ret.getEvents().get(0).getName(), ret.getTasks()));
		
		return ret;
	}
	
	/**
	 * Fetches the matching task configurations for the given capabilities.
	 * 
	 * @param capabilities
	 * @return
	 */
	private List<LmapTaskDto> getTaskListForCapabilities(final LmapCapabilityDto capabilities) {
		final List<LmapTaskDto> ret = new ArrayList<>();
		
		for (LmapCapabilityTaskDto capability : capabilities.getTasks()) {
			final MeasurementTypeDto type;
			try {
				type = MeasurementTypeDto.valueOf(capability.getTaskName().toUpperCase());
			} catch(IllegalArgumentException ex) {
				logger.error(String.format("Unknown measurement type of name %s requested. Ignoring.", capability.getTaskName()));
				continue;
			}
			LmapTaskDto task = getMeasurementTaskConfiguration(type, capability.getVersion());
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
	
	private LmapTaskDto getMeasurementTaskConfiguration(final MeasurementTypeDto name, final String version) {
		if (name == null || version == null) {
			return null;
		}
		
		final List<String> tagList = new ArrayList<String>();
		tagList.add(version);
		
		final LmapTaskDto ret = storageService.getTaskDto(name, version);
		
		if (ret != null) {
			ret.setTagList(tagList);
		}

		return ret;
	}
}
