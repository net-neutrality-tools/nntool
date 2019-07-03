package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.temporal.ChronoUnit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapReportDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report.LmapResultDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.QoSMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SpeedMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.SubMeasurementResult;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ConnectionInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.TrafficDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ConnectionInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurementType;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSResult;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Traffic;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Mapper(componentModel = "spring", imports = ChronoUnit.class)
public interface LmapReportModelMapper extends DateTimeMapper, UuidMapper {
	
	public static final String QOS_TEST_UID_KEY = "qos_test_uid";
	public static final String QOS_TEST_TYPE_KEY = "test_type";

	// Explicit map method not necessary if all fields have the same name
//	@Mappings({
//		@Mapping(source = "time", target = "time"),
//		@Mapping(source = "relativeTimeNs", target = "relativeTimeNs"),
//		
//		@Mapping(source = "accuracy", 	target = "accuracy"),
//		@Mapping(source = "altitude", 	target = "altitude"),
//		@Mapping(source = "heading", 	target = "heading"),
//		@Mapping(source = "speed", 		target = "speed"),
//		@Mapping(source = "provider", 	target = "provider"),
//		@Mapping(source = "latitude", 	target = "latitude"),
//		@Mapping(source = "longitude", 	target = "longitude")
//	})
//	GeoLocation map(GeoLocationDto geoLocationDto);
	
//	@Mappings({
//		@Mapping(source = "time", target = "time"),
//		@Mapping(source = "relativeTimeNs", target = "relativeTimeNs"),
//		
//		private Integer networkTypeId;
//		private LocalDateTime time;
//	    private Long relativeTimeNs;
//		private Integer wifiLinkSpeedBps;
//		private Integer wifiRssiDbm;
//		private Integer signalStrength2g3gDbm;
//	    private Integer lteRsrpDbm;
//	    private Integer lteRsrqDb;
//	    private Integer lteRssnrDb;
//	    private Integer lteCqi;
//	    private String wifiSsid;
//	    private String wifiBssid;
//	})
//	Signal map(SignalDto signalDto);
	
	@Mappings({
		@Mapping(target = "uuid", ignore = true),
		
		@Mapping(target = "id", ignore = true),
		@Mapping(target = "rev", ignore = true),
		
		@Mapping(source = "date", target = "submitTime"),
		@Mapping(source = "agentId", target = "agentInfo.uuid"),
		//@Mapping(source = "groupId", target = "groupId"),
		//@Mapping(source = "measurementPoint", target = "measurementPoint"),
		
//		@JsonProperty(required = true, value = "result")
//		private List<LmapResultDto<?>> results = new ArrayList<>();
		
		// uuid -> generate
		// openDataUuid -> generate
		// source?
		// tag?
		
		// uuid
		// openDataUuid
		// tag
		// -> measurements
		// source
		// -> measurementTime
		// -> geoLocationInfo
		// -> clientInfo
		// -> deviceInfo
		// -> networkInfo
		
		// ApiRequestInfo

		@Mapping(source = "additionalRequestInfo.language", 	target = "agentInfo.language"),
		@Mapping(source = "additionalRequestInfo.timezone", 	target = "agentInfo.timezone"),		
		@Mapping(source = "additionalRequestInfo.agentType", 	target = "agentInfo.type"),
		@Mapping(source = "additionalRequestInfo.osName", 		target = "deviceInfo.osInfo.name"),
		@Mapping(source = "additionalRequestInfo.osVersion", 	target = "deviceInfo.osInfo.version"),
		@Mapping(source = "additionalRequestInfo.apiLevel", 	target = "deviceInfo.osInfo.apiLevel"),
		@Mapping(source = "additionalRequestInfo.codeName", 	target = "deviceInfo.codeName"),
		@Mapping(source = "additionalRequestInfo.model", 		target = "deviceInfo.model"),
		@Mapping(target = "deviceInfo.fullName", ignore = true), // get from database
		@Mapping(source = "additionalRequestInfo.appVersionName", target = "agentInfo.appVersionName"),
		@Mapping(source = "additionalRequestInfo.appVersionCode", target = "agentInfo.appVersionCode"),
		@Mapping(source = "additionalRequestInfo.appGitRevision", target = "agentInfo.appGitRevision"),
		
		// TimeBasedResultDto
		
		@Mapping(source = "timeBasedResult.startTime", 	target = "measurementTime.startTime"),
		@Mapping(source = "timeBasedResult.endTime", 	target = "measurementTime.endTime"),
		@Mapping(target = "measurementTime.durationNs", expression="java(measurementTime == null ?"
				+ " null : measurementTime.getStartTime() == null ?"
				+ " null : measurementTime.getEndTime() == null ?"
				+ " null : ChronoUnit.NANOS.between(measurementTime.getStartTime(), measurementTime.getEndTime()))"),
		
		@Mapping(source = "timeBasedResult.geoLocations", target = "geoLocationInfo.geoLocations"),
//		//@Mapping(source = "timeBasedResult.cpuUsage", target = "deviceInfo.osInfo.cpuUsage"),
//		//@Mapping(source = "timeBasedResult.memUsage", target = "deviceInfo.osInfo.memUsage"),
//		//@Mapping(source = "timeBasedResult.networkPointsInTime", target = "networkPointsInTime"),
		@Mapping(target = "networkInfo.networkPointsInTime", ignore = true), // TODO
		
		@Mapping(source = "timeBasedResult.cellLocations", target = "networkInfo.cellLocationInfo.cellLocations"),
		@Mapping(source = "timeBasedResult.signals", target = "networkInfo.signalInfo.signals"),
		@Mapping(expression = "java(parseMeasurements(lmapReportDto))", target = "measurements")
	})
	Measurement map(LmapReportDto lmapReportDto);
	
	@Mappings ({
		@Mapping(source="relativeStartTimeNs", target="measurementTime.relativeStartTimeNs"),
		@Mapping(source="relativeEndTimeNs", target="measurementTime.relativeEndTimeNs"),
		@Mapping(target = "measurementTime.durationNs", expression="java(speedMeasurementResult == null ?"
				+ " null : speedMeasurementResult.getRelativeStartTimeNs() == null ?"
				+ " null : speedMeasurementResult.getRelativeEndTimeNs() == null ?"
				+ " null : speedMeasurementResult.getRelativeEndTimeNs() - speedMeasurementResult.getRelativeStartTimeNs())"),
		@Mapping(source="downloadRawData", target="speedRawData.download"),
		@Mapping(source="uploadRawData", target="speedRawData.upload"),
		@Mapping(source="connectionInfo", target="connectionInfo"),
		@Mapping(source="status", target="statusInfo.status"),
		@Mapping(source="reason", target="statusInfo.reason"),
		@Mapping(expression="java(parseAverageDownload(subMeasurementResult))", target="throughputAvgDownloadBps"),
		@Mapping(expression="java(parseAverageUpload(subMeasurementResult))", target="throughputAvgUploadBps"),
		@Mapping(expression="java(speedMeasurement.getThroughputAvgDownloadBps() == null ? "
				+ "null : Math.log10(speedMeasurement.getThroughputAvgDownloadBps()))", target="throughputAvgDownloadLog"),
		@Mapping(expression="java(speedMeasurement.getThroughputAvgUploadBps() == null ? "
				+ "null : Math.log10(speedMeasurement.getThroughputAvgUploadBps()))", target="throughputAvgUploadLog"),
	})
	SpeedMeasurement map (SpeedMeasurementResult subMeasurementResult);
	
	@Mappings ({
		@Mapping(source="relativeStartTimeNs", target="measurementTime.relativeStartTimeNs"),
		@Mapping(source="relativeEndTimeNs", target="measurementTime.relativeEndTimeNs"),
		@Mapping(source="status", target="statusInfo.status"),
		@Mapping(source="reason", target="statusInfo.reason"),
		@Mapping(target = "measurementTime.durationNs", expression="java(qoSMeasurementResult == null ?"
				+ " null : qoSMeasurementResult.getRelativeStartTimeNs() == null ?"
				+ " null : qoSMeasurementResult.getRelativeEndTimeNs() == null ?"
				+ " null : qoSMeasurementResult.getRelativeEndTimeNs() - qoSMeasurementResult.getRelativeStartTimeNs())"),
		@Mapping(source="status", target="statusInfo"),
		@Mapping(expression="java(parseQoSResult(subMeasurementResult))", target="results")
	})
	QoSMeasurement map (QoSMeasurementResult subMeasurementResult);
	
	@Mappings ({
		@Mapping(source="agentInterfaceTotalTraffic", target="clientInterfaceTotalTraffic"),
		@Mapping(source="agentInterfaceDownloadMeasurementTraffic", target="clientInterfaceDownloadMeasurementTraffic"),
		@Mapping(source="agentInterfaceUploadMeasurementTraffic", target="clientInterfaceUploadMeasurementTraffic"),
		@Mapping(source="requestedNumStreamsDownload", target="numStreamsInfo.requestedNumStreamsDownload"),
		@Mapping(source="requestedNumStreamsUpload", target="numStreamsInfo.requestedNumStreamsUpload"),
		@Mapping(source="actualNumStreamsDownload", target="numStreamsInfo.actualNumStreamsDownload"),
		@Mapping(source="actualNumStreamsUpload", target="numStreamsInfo.actualNumStreamsUpload"),
	})
	ConnectionInfo map (ConnectionInfoDto connectionInfoDto);
	
	Traffic map (TrafficDto trafficDto);
	
	default Long parseAverageDownload (final SpeedMeasurementResult result) {
		if (result.getBytesDownload() == null || result.getDurationDownloadNs() == null) {
			return 0L;
		}
		
		return (long) (result.getBytesDownload() * 8 / (result.getDurationDownloadNs() / 1e9));
	}
	
	default Long parseAverageUpload (final SpeedMeasurementResult result) {
		if (result.getBytesUpload() == null || result.getDurationUploadNs() == null) {
			return 0L;
		}
		
		return (long) (result.getBytesUpload() * 8 / (result.getDurationUploadNs() / 1e9));
	}
	
	default Map<MeasurementTypeDto, SubMeasurement> parseMeasurements (LmapReportDto lmapReportDto) {
		Map<MeasurementTypeDto, SubMeasurement> ret = new HashMap<>();
		
		for (LmapResultDto<? extends SubMeasurementResult> resList : lmapReportDto.getResults()) {
			for (SubMeasurementResult res : resList.getResults()) {
				if (res instanceof SpeedMeasurementResult) {
					ret.put(MeasurementTypeDto.SPEED, this.map((SpeedMeasurementResult) res));
				} else if (res instanceof QoSMeasurementResult) {
					ret.put(MeasurementTypeDto.QOS, this.map((QoSMeasurementResult) res));
				}
			}
		}
		
		return ret;
	}
	
	default List<QoSResult> parseQoSResult (QoSMeasurementResult qosMeasurement) {
		List<QoSResult> ret = new ArrayList<>();
		
		List<Map<String, Object>> mapList = qosMeasurement.getObjectiveResults();
		if (mapList == null) {
			return ret;
		}
		for (Map<String, Object> map : mapList) {
			final QoSResult res = new QoSResult();
			Object val = map.get(QOS_TEST_TYPE_KEY);
			if (val != null) {
				try {
					res.setType(QoSMeasurementType.valueOf(val.toString().toUpperCase()));
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				}
			}
			val = map.get(QOS_TEST_UID_KEY);
			if (val != null) {
				try {
					res.setObjectiveId(Long.parseLong(val.toString()));
				} catch (IllegalArgumentException ex) {
					
				}
			}
			res.setResults(map);
			ret.add(res);
		}
		
		return ret;
	}
	
}
