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

package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.ApiRequestInfo;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common.LmapOptionDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.QoSMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation.SpeedMeasurementTypeParameters;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultNetworkPointInTimeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.TimeBasedResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.PointInTimeValueDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.QoSMeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ReasonDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.RttInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SpeedMeasurementRawDataItemDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.StatusDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.WebSocketInfoDto;

/**
 * This unit test is used to generate the example configuration file of the report model for milestone 1 deliverable 3.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class LmapReportModelExampleTest extends AbstractLmapExampleTest {

	/**
	 * 
	 */
	@Test
	public void testGenerateReportModel() {
		final LmapReportDto lmapReportDto = new LmapReportDto();
		
		///
		
		lmapReportDto.setDate(LocalDateTime.now());
		lmapReportDto.setAgentId("60c42144-e7f2-47ca-acfd-5447946a6f5e");
		lmapReportDto.setGroupId("example_group");
		lmapReportDto.setMeasurementPoint("_measurement_point_");
		
		///
		
		final List<LmapResultDto> results = new ArrayList<>();
		
		final LmapResultDto speedResultDto = new LmapResultDto();
		speedResultDto.setSchedule("immediate_speed_qos_measurement_schedule");
		speedResultDto.setAction("speed_measurement_action");
		speedResultDto.setTask(MeasurementTypeDto.SPEED.name());
		//speedResultDto.setParameters(Object parameters);
		
        final SpeedMeasurementTypeParameters speedParams = new SpeedMeasurementTypeParameters();
        /*
        final SpeedMeasurementTypeParameters.Durations dur = new SpeedMeasurementTypeParameters.Durations();
        dur.setDownload(10);
        dur.setDownloadSlowStart(3);
        dur.setUpload(10);
        dur.setUploadSlowStart(3);
        //speedParams.setDurations(dur);
        */

        /*
        final SpeedMeasurementTypeParameters.Flows flows = new SpeedMeasurementTypeParameters.Flows();
        flows.setDownload(3);
        flows.setDownloadSlowStart(3);
        flows.setUpload(3);
        flows.setUploadSlowStart(3);
        flows.setRtt(3);
        */
        //speedParams.setFlows(flows);
        speedParams.setRttCount(10);
        speedParams.setJavascriptMeasurementCodeUrl("measurement.berec.eu");

        final SpeedMeasurementTypeParameters.MeasurementServerConfig conf = new SpeedMeasurementTypeParameters.MeasurementServerConfig();
        conf.setBaseUrl("berec.eu");
        speedParams.setMeasurementServerConfig(conf);
        
        LmapOptionDto option = new LmapOptionDto();
		option.setName(MeasurementTypeDto.SPEED.name());
		option.setMeasurementParameters(speedParams);
		speedResultDto.getOptions().add(option);
		//speedResultDto.setTags(List<String> tags);
		speedResultDto.setStart(LocalDateTime.now().minusHours(1).minusMinutes(2));
		speedResultDto.setEnd(LocalDateTime.now().minusHours(1));
		speedResultDto.setEvent(speedResultDto.getStart());
		//speedResultDto.setCycleNumber(String cycleNumber);
		speedResultDto.setStatus(0);
		//speedResultDto.setConflict(List<LmapReportResultConflictDto> conflict);
		
		final SpeedMeasurementResult speedMeasurementResult = new SpeedMeasurementResult();
		speedMeasurementResult.setRelativeStartTimeNs(1234L);
		speedMeasurementResult.setRelativeEndTimeNs((long)(4.6e10 + 10));
		speedMeasurementResult.setStatus(StatusDto.FINISHED);
		speedMeasurementResult.setBytesDownload(537484L);
		speedMeasurementResult.setBytesDownloadIncludingSlowStart(537889L);
		speedMeasurementResult.setBytesUpload(12396739L);
		speedMeasurementResult.setBytesUploadIncludingSlowStart(12496739L);
		speedMeasurementResult.setDurationRttNs((long)(1e10 + 8));
		speedMeasurementResult.setDurationDownloadNs((long)(1.4e10+10));
		speedMeasurementResult.setDurationUploadNs((long)(2.1e10 + 10));
		speedMeasurementResult.setRelativeStartTimeRttNs((long)(0.4e10+10));
		speedMeasurementResult.setRelativeStartTimeDownloadNs((long)(1.8e10+10));
		speedMeasurementResult.setRelativeStartTimeUploadNs((long)(3.5e10+10));

        final RttInfoDto rttInfo = new RttInfoDto();
        rttInfo.setRequestedNumPackets(2);
        rttInfo.setNumSent(2);
        rttInfo.setNumReceived(2);
        rttInfo.setNumError(0);
        rttInfo.setNumMissing(0);
        rttInfo.setPacketSize(12);
        final List<RttDto> rttList = new ArrayList<>();
        RttDto rtt = new RttDto();
        rtt.setRttNs(1436L);
        rtt.setRelativeTimeNs((long)(0.6e10+10));
        rttList.add(rtt);
        rtt = new RttDto();
        rtt.setRttNs(874L);
        rtt.setRelativeTimeNs((long)(1.2e10+10));
        rttList.add(rtt);
        rttInfo.setRtts(rttList);
        speedMeasurementResult.setRttInfo(rttInfo);

        final List<SpeedMeasurementRawDataItemDto> rawDownloadList = new ArrayList<>();
        SpeedMeasurementRawDataItemDto raw = new SpeedMeasurementRawDataItemDto();
        raw.setStreamId(0);
        raw.setRelativeTimeNs((long)(1.9e10+10));
        raw.setBytes(1983L);
        rawDownloadList.add(raw);
        raw = new SpeedMeasurementRawDataItemDto();
        raw.setStreamId(1);
        raw.setRelativeTimeNs((long)(2.4e10+10));
        raw.setBytes(198743L);
        rawDownloadList.add(raw);
        raw = new SpeedMeasurementRawDataItemDto();
        raw.setStreamId(0);
        raw.setRelativeTimeNs((long)(3.2e10+10));
        raw.setBytes(19838484L);
        rawDownloadList.add(raw);
        speedMeasurementResult.setDownloadRawData(rawDownloadList);

        final List<SpeedMeasurementRawDataItemDto> rawUploadList = new ArrayList<>();
        raw = new SpeedMeasurementRawDataItemDto();
        raw.setStreamId(0);
        raw.setRelativeTimeNs((long)(3.9e10+10));
        raw.setBytes(1983L);
        rawUploadList.add(raw);
        raw = new SpeedMeasurementRawDataItemDto();
        raw.setStreamId(1);
        raw.setRelativeTimeNs((long)(4.2e10+10));
        raw.setBytes(198743L);
        rawUploadList.add(raw);
        speedMeasurementResult.setUploadRawData(rawUploadList);

        final ConnectionInfoDto connect = new ConnectionInfoDto();
        connect.setAddress("42.43.44.45");
        connect.setPort(8080);
        connect.setEncrypted(false);
        TrafficDto traffic = new TrafficDto();
        traffic.setBytesRx(467346731234L);
        traffic.setBytesTx(3672367823234L);
        connect.setAgentInterfaceTotalTraffic(traffic);
        traffic = new TrafficDto();
        traffic.setBytesTx(8989347893434L);
        traffic.setBytesRx(123123213L);
        connect.setAgentInterfaceDownloadMeasurementTraffic(traffic);
        traffic = new TrafficDto();
        traffic.setBytesTx(12313L);
        traffic.setBytesRx(889893488998L);
        connect.setAgentInterfaceUploadMeasurementTraffic(traffic);
        connect.setRequestedNumStreamsDownload(2);
        connect.setRequestedNumStreamsUpload(2);
        connect.setActualNumStreamsDownload(2);
        connect.setActualNumStreamsUpload(2);
        connect.setTcpOptSackRequested(true);
        connect.setTcpOptWscaleRequested(false);
        connect.setServerMss(23352);
        connect.setServerMtu(2423);
        WebSocketInfoDto webSocket = new WebSocketInfoDto();
        webSocket.setFrameSize(24423);
        webSocket.setFrameCount(212);
        webSocket.setFrameCountIncludingSlowStart(243);
        webSocket.setOverhead(1234);
        webSocket.setOverheadPerFrame(2);
        webSocket.setOverheadIncludingSlowStart(12);
        connect.setWebSocketInfoDownload(webSocket);
        webSocket = new WebSocketInfoDto();
        webSocket.setFrameSize(25123);
        webSocket.setFrameCount(243);
        webSocket.setFrameCountIncludingSlowStart(263);
        webSocket.setOverhead(623);
        webSocket.setOverheadPerFrame(1);
        webSocket.setOverheadIncludingSlowStart(21);
        connect.setWebSocketInfoUpload(webSocket);
        speedMeasurementResult.setConnectionInfo(connect);

//		final LmapTableDto<SpeedMeasurementResult> tableSpeed = new LmapTableDto<>();
//		tableSpeed.setColumns(Arrays.asList("result"));
//		tableSpeed.setRows(Arrays.asList(new LmapTableRowDto<SpeedMeasurementResult>(Arrays.asList(speedMeasurementResult))));
//		
//		speedResultDto.setTable(Arrays.asList(tableSpeed));
        
        speedResultDto.getResults().add(speedMeasurementResult);
		
		results.add(speedResultDto);
		
		//
		final LmapResultDto qosResultDto = new LmapResultDto();
		
		qosResultDto.setSchedule("immediate_speed_qos_measurement_schedule");
		qosResultDto.setAction("qos_measurement_action");
		qosResultDto.setTask(MeasurementTypeDto.QOS.name());
		//qosResultDto.setParameters(Object parameters);
		final QoSMeasurementTypeParameters qosParams = new QoSMeasurementTypeParameters();
        final Map<QoSMeasurementTypeDto, List<Map<String, Object>>> objectives = new HashMap<>();
        objectives.put(QoSMeasurementTypeDto.TCP, new ArrayList<Map<String, Object>>());
        option = new LmapOptionDto();
		option.setName(MeasurementTypeDto.QOS.name());
		option.setMeasurementParameters(qosParams);
		qosResultDto.getOptions().add(option);
		//qosResultDto.setTags(List<String> tags);
		qosResultDto.setStart(LocalDateTime.now().minusHours(1).minusMinutes(2));
		qosResultDto.setEnd(LocalDateTime.now().minusHours(1));
		qosResultDto.setEvent(qosResultDto.getStart());
		//qosResultDto.setCycleNumber(String cycleNumber);
		qosResultDto.setStatus(1);
		//qosResultDto.setConflict(List<LmapReportResultConflictDto> conflict);
		
		final QoSMeasurementResult qosMeasurementResult = new QoSMeasurementResult();
		qosMeasurementResult.setStatus(StatusDto.FAILED);
		qosMeasurementResult.setReason(ReasonDto.UNABLE_TO_CONNECT);
		qosMeasurementResult.setRelativeStartTimeNs((long)(4.6e10 + 10));
		qosMeasurementResult.setRelativeEndTimeNs((long)(11.7e10 + 10));
		
//		final LmapTableDto<QoSMeasurementResult> tableQos = new LmapTableDto<>();
//		tableQos.setColumns(Arrays.asList("result"));
//		tableQos.setRows(Arrays.asList(new LmapTableRowDto<QoSMeasurementResult>(Arrays.asList(qosMeasurementResult))));
		
		
		qosResultDto.getResults().add(qosMeasurementResult);
		
		results.add(qosResultDto);
		
		lmapReportDto.setResults(results);
		
		///
		
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
		//apiRequestInfo.setGeoLocation(GeoLocationDto geoLocation);
		
		lmapReportDto.setAdditionalRequestInfo(apiRequestInfo);
		
		///
		
		final TimeBasedResultDto timeBasedResultDto = new TimeBasedResultDto();

		timeBasedResultDto.setStartTime(LocalDateTime.now().minusHours(1).minusMinutes(2));
		timeBasedResultDto.setEndTime(LocalDateTime.now().minusHours(1));
		timeBasedResultDto.setDurationNs(2 * 60000000000L);
		
		//
		
		final List<GeoLocationDto> geoLocList = new ArrayList<>();
        GeoLocationDto loc = new GeoLocationDto();
        loc.setTime(new LocalDateTime(2011, 2, 6, 22, 42, 14));
        loc.setAccuracy(2.1);
        loc.setAltitude(184.3);
        loc.setHeading(0.0);
        loc.setSpeed(0.0);
        loc.setProvider("accurate location provider");
        loc.setLatitude(32.747895);
        loc.setLongitude(-97.092505);
        loc.setRelativeTimeNs((long) (1.4e10 + 10));
        geoLocList.add(loc);
        
        loc = new GeoLocationDto();
        loc.setTime(new LocalDateTime(2011, 2, 6, 22, 42, 38));
        loc.setAccuracy(12.1);
        loc.setAltitude(184.7);
        loc.setHeading(0.0);
        loc.setSpeed(0.0);
        loc.setProvider("accurate location provider");
        loc.setLatitude(32.748071);
        loc.setLongitude(-97.092108);
        loc.setRelativeTimeNs((long) (3.8e10 + 10));
        geoLocList.add(loc);
		
		timeBasedResultDto.setGeoLocations(geoLocList);
		
		//
        
        final List<PointInTimeValueDto<Double>> cpuList = new ArrayList<>();
        PointInTimeValueDto<Double> point = new PointInTimeValueDto<>();
        point.setValue(42.4242);
        point.setRelativeTimeNs((long)(3.8e10 + 10));
        cpuList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(67.2353);
        point.setRelativeTimeNs((long)(4.2e10 + 10));
        cpuList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(96.1002);
        point.setRelativeTimeNs((long)(5.6e10 + 10));
        cpuList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(77.3543);
        point.setRelativeTimeNs((long)(7.8e10 + 10));
		
		timeBasedResultDto.setCpuUsage(cpuList);
		
		//
		
        final List<PointInTimeValueDto<Double>> memList = new ArrayList<>();
        point = new PointInTimeValueDto<>();
        point.setValue(42.4242);
        point.setRelativeTimeNs((long)(4.2e10 + 10));
        memList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(21.2353);
        point.setRelativeTimeNs((long)(4.8e10 + 10));
        memList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(36.4545);
        point.setRelativeTimeNs((long)(7.3e10 + 10));
        memList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(65.223);
        point.setRelativeTimeNs((long)(9.4e10 + 10));
        memList.add(point);
        point = new PointInTimeValueDto<>();
        point.setValue(73.563);
        point.setRelativeTimeNs((long)(9.8e10 + 10));
        memList.add(point);
        
		timeBasedResultDto.setMemUsage(memList);
		
		//
		
		final List<MeasurementResultNetworkPointInTimeDto> networkList = new ArrayList<>();
        final MeasurementResultNetworkPointInTimeDto netPoint = new MeasurementResultNetworkPointInTimeDto();
        
        netPoint.setNetworkTypeId(99);
        netPoint.setSsid("Arlington Public WiFi");
        netPoint.setBssid("45:87:66:45:32:aa");
        netPoint.setRelativeTimeNs((long)(.1e10+10));
        netPoint.setTime(new LocalDateTime(2011, 2, 6, 22, 42, 14));
        networkList.add(netPoint);

		timeBasedResultDto.setNetworkPointsInTime(networkList);
		
		//
		
		//timeBasedResultDto.setCellLocations(List<CellInfoDto> cellLocations);
		
		//
		
		final List<SignalDto> signalList = new ArrayList<>();
        SignalDto signal = new SignalDto();
        signal.setNetworkTypeId(99);
        signal.setRelativeTimeNs((long)(2.4e10+10));
        netPoint.setTime(new LocalDateTime(2011, 2, 6, 22, 42, 54));
        signal.setWifiBssid("45:87:66:45:32:aa");
        signal.setWifiSsid("Arlington Public WiFi");
        signal.setWifiLinkSpeedBps((int) (2.5e+7));
        signal.setWifiRssiDbm(-57);
        signalList.add(signal);
        
        signal = new SignalDto();
        signal.setNetworkTypeId(99);
        signal.setRelativeTimeNs((long)(3.2e10+10));
        netPoint.setTime(new LocalDateTime(2011, 2, 6, 22, 43, 02));
        signal.setWifiBssid("45:87:66:45:32:aa");
        signal.setWifiSsid("Arlington Public WiFi");
        signal.setWifiLinkSpeedBps((int) (2.5e+7));
        signal.setWifiRssiDbm(-52);
        signalList.add(signal);
		
		timeBasedResultDto.setSignals(signalList);
		
		//
		
		lmapReportDto.setTimeBasedResult(timeBasedResultDto);

		///
			
		System.out.println(objectToJson(lmapReportDto));
	}
}
