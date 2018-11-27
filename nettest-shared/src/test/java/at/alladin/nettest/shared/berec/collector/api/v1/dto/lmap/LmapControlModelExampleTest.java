package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapActionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapAgentDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapCapabilityTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapControlDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapEventDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapScheduleDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapStopDurationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control.LmapTaskDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;

/**
 * This unit test is used to generate the example configuration file of the control model for milestone 1 deliverable 3.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class LmapControlModelExampleTest extends AbstractLmapExampleTest {

	@Test
	public void produceLmapMeasurementInitiationRequest() {
		final LmapControlDto dto = new LmapControlDto();
		final LmapAgentDto agent = new LmapAgentDto();
//		agent.setReportAgentId(true);
		agent.setAgentId("add895c3-9a69-472b-ae38-045266df52fd");
//		agent.setReportGroupId(true);
		agent.setGroupId("berec_reference_system");
		agent.setLastStarted(new LocalDateTime(2011, 2, 6, 22, 42, 14));
		dto.setAgent(agent);
		
		final LmapCapabilityDto capa = new LmapCapabilityDto();
		List<String> tagList = new ArrayList<>();
		tagList.add("version_library:1.2.1");
		tagList.add("version_protocol:2.34.5");
		tagList.add("tag:include_qos");
		tagList.add("test_counter:42");
		capa.setTag(tagList);
		capa.setVersion("berec measurment tool - version: ma_v1.0.0_c_v2.1.4");
		List<LmapCapabilityTaskDto> tasks = new ArrayList<>();
		
		LmapCapabilityTaskDto taskObj = new LmapCapabilityTaskDto();
		taskObj.setTaskName(MeasurementTypeDto.SPEED.name());
		taskObj.setVersion("2.1.4");
		tasks.add(taskObj);
		
		taskObj = new LmapCapabilityTaskDto();
		taskObj.setTaskName(MeasurementTypeDto.QOS.name());
		taskObj.setVersion("3.3");
		tasks.add(taskObj);
		capa.setTasks(tasks);

		dto.setCapabilities(capa);
		
//		final List<TaskDto> taskList = new ArrayList<>();
//		
//		TaskDto taskDto = new TaskDto();
//		taskDto.setTaskName(MeasurementType.SPEED.name());
//		taskList.add(taskDto);
//		
//		taskDto = new TaskDto();
//		taskDto.setTaskName(MeasurementType.QOS.name());
//		OptionDto option = new OptionDto();
//		option.setId("TCP_QOS_ID");
//		taskDto.getOptions().add(option);
//		option = new OptionDto();
//		option.setId("DNS_QOS_ID");
//		taskDto.getOptions().add(option);
//		taskList.add(taskDto);
//		dto.setTasks(taskList);
		
		final ApiRequestInfo apiRequestInfo = new ApiRequestInfo();
		
		apiRequestInfo.setLanguage("en_US");
		apiRequestInfo.setTimezone("Europe/Vienna");
		apiRequestInfo.setAgentType(MeasurementAgentTypeDto.MOBILE);
		apiRequestInfo.setOsName("Android");
		apiRequestInfo.setOsVersion("8.1");
		apiRequestInfo.setApiLevel("27");
		apiRequestInfo.setCodeName("bullhead");
		apiRequestInfo.setModel("MOTOG3");
		apiRequestInfo.setAppVersionName("1.0.0");
		apiRequestInfo.setAppVersionCode(42);
		apiRequestInfo.setAppGitRevision("cba56293");
		
		dto.setAdditionalRequestInfo(apiRequestInfo);
		
		String res = objectToJson(dto);
//		System.out.println(res);
	}

    @Test
    public void produceLmapMeasurementInitiationResponse() {
    	final LmapControlDto dto = new LmapControlDto();
		final LmapAgentDto agent = new LmapAgentDto();
//		agent.setReportAgentId(true);
		agent.setAgentId("add895c3-9a69-472b-ae38-045266df52fd");
//		agent.setReportGroupId(true);
		agent.setGroupId("berec_reference_system");
		agent.setLastStarted(new LocalDateTime(2011, 2, 6, 22, 42, 14));
		dto.setAgent(agent);

		final LmapScheduleDto schedule = new LmapScheduleDto();
		schedule.setName("immediate_speed_qos_measurement_schedule");
		schedule.setStart("immediate_event");
		final LmapStopDurationDto stop = new LmapStopDurationDto();
		stop.setDuration(180000);
		schedule.setStop(stop);
		schedule.setExecutionMode(LmapScheduleDto.ExecutionMode.SEQUENTIAL);
		List<String> tagList = schedule.getTags();
		tagList.add("Base");
		tagList.add("includes QoS");

		LmapActionDto action = new LmapActionDto();
		action.setName("speed_measurement_action");
		action.setTaskName(MeasurementTypeDto.SPEED.name());
		schedule.getActions().add(action);
		action = new LmapActionDto();
		action.setName("qos_measurement_action");
		action.setTaskName(MeasurementTypeDto.QOS.name());
		schedule.getActions().add(action);
		dto.getSchedules().add(schedule);
		
		LmapEventDto ev = new LmapEventDto();
		ev.setName("immediate_event");
		dto.getEvents().add(ev);

		final LmapCapabilityDto capa = new LmapCapabilityDto();
		tagList = new ArrayList<>();
		tagList.add("Tag 1");
		tagList.add("Other tag");
		capa.setTag(tagList);
		capa.setVersion("berec measurment tool - version: ma_v1.0.0_c_v2.1.4");
		List<LmapCapabilityTaskDto> tasks = new ArrayList<>();
		
		LmapCapabilityTaskDto taskObj = new LmapCapabilityTaskDto();
		taskObj.setTaskName(MeasurementTypeDto.SPEED.name());
		taskObj.setVersion("2.1.4");
		tasks.add(taskObj);
		
		taskObj = new LmapCapabilityTaskDto();
		taskObj.setTaskName(MeasurementTypeDto.QOS.name());
		taskObj.setVersion("3.3");
		tasks.add(taskObj);
		capa.setTasks(tasks);

		dto.setCapabilities(capa);
		
//		final List<TaskDto> taskList = new ArrayList<>();
//		
//		TaskDto taskDto = new TaskDto();
//		taskDto.setTaskName(MeasurementType.SPEED.name());
//		taskList.add(taskDto);
//		
//		taskDto = new TaskDto();
//		taskDto.setTaskName(MeasurementType.QOS.name());
//		OptionDto option = new OptionDto();
//		option.setId("TCP_QOS_ID");
//		taskDto.getOptions().add(option);
//		option = new OptionDto();
//		option.setId("DNS_QOS_ID");
//		taskDto.getOptions().add(option);
//		taskList.add(taskDto);
//		dto.setTasks(taskList);
		
		final QoSMeasurementTypeParameters qosParams = new QoSMeasurementTypeParameters();
        final Map<QoSMeasurementTypeDto, List<Map<String, Object>>> objectives = new HashMap<>();
        objectives.put(QoSMeasurementTypeDto.TCP, new ArrayList<Map<String, Object>>());
        qosParams.setObjectives(objectives);

        final SpeedMeasurementTypeParameters speedParams = new SpeedMeasurementTypeParameters();
        final SpeedMeasurementTypeParameters.Durations dur = new SpeedMeasurementTypeParameters.Durations();
        dur.setDownload(10);
        dur.setDownloadSlowStart(3);
        dur.setUpload(10);
        dur.setUploadSlowStart(3);
        speedParams.setDurations(dur);

        final SpeedMeasurementTypeParameters.Flows flows = new SpeedMeasurementTypeParameters.Flows();
        flows.setDownload(3);
        flows.setDownloadSlowStart(3);
        flows.setUpload(3);
        flows.setUploadSlowStart(3);
        flows.setRtt(3);
        speedParams.setFlows(flows);
        speedParams.setRttCount(10);
        speedParams.setJavascriptMeasurementCodeUrl("measurement.berec.eu");

        final SpeedMeasurementTypeParameters.MeasurementServerConfig conf = new SpeedMeasurementTypeParameters.MeasurementServerConfig();
        conf.setBaseUrl("berec.eu");
        speedParams.setMeasurementServerConfig(conf);
        
        LmapTaskDto task = new LmapTaskDto();
        task.setName(MeasurementTypeDto.SPEED.name());
		LmapOptionDto option = new LmapOptionDto();
		option.setName(MeasurementTypeDto.SPEED.name());
		option.setMeasurementParameters(speedParams);
		task.getOptions().add(option);
		
		option = new LmapOptionDto();
		option.setName("result_collector_base_url");
		option.setValue("collector.berec.eu");
		task.getOptions().add(option);
		
		option = new LmapOptionDto();
		option.setName("measurement_uuid");
		option.setValue("25c50ecc-f0f3-456f-a6c5-e738496c0bf3");
		task.getOptions().add(option);
		dto.getTasks().add(task);
		
		task = new LmapTaskDto();
        task.setName(MeasurementTypeDto.QOS.name());
		option = new LmapOptionDto();
		option.setName(MeasurementTypeDto.QOS.name());
		option.setMeasurementParameters(qosParams);
		task.getOptions().add(option);
		
		option = new LmapOptionDto();
		option.setName("result_collector_base_url");
		option.setValue("collector.berec.eu");
		task.getOptions().add(option);
		
		option = new LmapOptionDto();
		option.setName("measurement_uuid");
		option.setValue("25c50ecc-f0f3-456f-a6c5-e738496c0bf3");
		task.getOptions().add(option);
		
		dto.getTasks().add(task);
        
		
		final ApiRequestInfo apiRequestInfo = new ApiRequestInfo();
		
		apiRequestInfo.setLanguage("en_US");
		apiRequestInfo.setTimezone("Europe/Vienna");
		apiRequestInfo.setAgentType(MeasurementAgentTypeDto.MOBILE);
		apiRequestInfo.setOsName("Android");
		apiRequestInfo.setOsVersion("8.1");
		apiRequestInfo.setApiLevel("27");
		apiRequestInfo.setCodeName("bullhead");
		apiRequestInfo.setModel("MOTOG3");
		apiRequestInfo.setAppVersionName("1.0.0");
		apiRequestInfo.setAppVersionCode(42);
		apiRequestInfo.setAppGitRevision("cba56293");
		
		dto.setAdditionalRequestInfo(apiRequestInfo);

		String res = objectToJson(dto);
		System.out.println(res);
    }
}
