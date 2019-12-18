package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import java.util.HashMap;
import java.util.Map;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ComputedNetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.OperatingSystemInfoDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.ComputedNetworkPointInTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.Measurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkPointInTime;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.OperatingSystemInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.QoSMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SpeedMeasurement;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.SubMeasurement;

@Mapper(componentModel = "spring", uses = { DateTimeMapper.class, ConnectionInfoMapper.class})
public interface FullMeasurementResponseMapper {

	@Mappings({
		@Mapping(source="measurementTime.startTime", target="startTime"),
		@Mapping(source="measurementTime.durationNs", target="durationNs"),
		@Mapping(source="measurementTime.endTime", target="endTime"),
		@Mapping(source="geoLocationInfo.geoLocations", target="geoLocations"),
		@Mapping(source = "geoLocationInfo.distanceMovedMetres", target="distanceMovedMetres"),
		@Mapping(expression="java(parseSubMeasurementMap(measurement.getMeasurements()))", target="measurements"),
		@Mapping(source = "networkInfo.computedNetworkInfo", target="computedNetworkInfo"),
		@Mapping(source = "qosAdvancedEvaluation", target="qosAdvancedEvaluation"),
	})
	FullMeasurementResponse map(Measurement measurement);
	
	@Mappings({
		@Mapping(source="measurementTime.relativeStartTimeNs", target="relativeStartTimeNs"),
		@Mapping(source="measurementTime.relativeEndTimeNs", target="relativeEndTimeNs"),
		@Mapping(source="measurementTime.durationNs", target="durationNs"),
		@Mapping(source="measurementTime.startTime", target="startTime"),
		@Mapping(source="measurementTime.endTime", target="endTime"),
		@Mapping(source="statusInfo.status", target="status"),
		@Mapping(source="statusInfo.reason", target="reason"),
		@Mapping(source="speedRawData.download", target="downloadRawData"),
		@Mapping(source="speedRawData.upload", target="uploadRawData"),
		@Mapping(source="rttInfo.address", target="rttInfo.address"),

		//rttInfo.maximumNs, minimumNs
	})
	FullSpeedMeasurement map(SpeedMeasurement measurement);
	
	@Mappings({
		@Mapping(source="measurementTime.relativeStartTimeNs", target="relativeStartTimeNs"),
		@Mapping(source="measurementTime.relativeEndTimeNs", target="relativeEndTimeNs"),
		@Mapping(source="measurementTime.durationNs", target="durationNs"),
		@Mapping(source="measurementTime.startTime", target="startTime"),
		@Mapping(source="measurementTime.endTime", target="endTime"),
		@Mapping(source="statusInfo.status", target="status"),
		@Mapping(source="statusInfo.reason", target="reason"),

		@Mapping(ignore = true, target = "keyToTranslationMap"),
		@Mapping(ignore = true, target = "qosTypeToDescriptionMap"),
		@Mapping(ignore = true, target = "results")
	})
	FullQoSMeasurement map(QoSMeasurement measurement);

	@Mappings({
		@Mapping(/*source=""*/ignore = true, target="cpuAverage"), // TODO
		@Mapping(/*source=""*/ignore = true, target="cpuMedian"), // TODO
		@Mapping(/*source=""*/ignore = true, target="cpuMin"), // TODO
		@Mapping(/*source=""*/ignore = true, target="cpuMax"), // TODO
		@Mapping(/*source=""*/ignore = true, target="memoryAverage"), // TODO
		@Mapping(/*source=""*/ignore = true, target="memoryMedian"), // TODO
		@Mapping(/*source=""*/ignore = true, target="memoryMin"), // TODO
		@Mapping(/*source=""*/ignore = true, target="memoryMax") // TODO
	})
	OperatingSystemInfoDto map(OperatingSystemInfo osInfo);

	@Mappings({
		@Mapping(source = "networkPointsInTime", target = "networkPointInTimeInfo"),
		@Mapping(source = "signalInfo.signals", target = "signals"),
	})
	NetworkInfoDto map(NetworkInfo networkInfo);

	@Mappings({
		@Mapping(source = "agentPrivateIp", target = "agentPrivateIp"),
		@Mapping(source = "agentPublicIp", target = "agentPublicIp"),
		@Mapping(source = "agentPublicIpCountryCode", target = "agentPublicIpCountryCode"),

		@Mapping(source = "networkType.category", target = "networkTypeCategory"),
		@Mapping(source = "networkType.groupName", target = "networkTypeGroupName"),
		@Mapping(source = "networkType.networkTypeId", target = "networkTypeId"),
		@Mapping(source = "networkType.name", target = "networkTypeName"),

		@Mapping(source = "providerInfo.countryCodeAsn", target = "countryCodeAsn"),
		@Mapping(source = "providerInfo.provider.name", target = "providerName"),
		@Mapping(source = "providerInfo.provider.shortName", target = "providerShortName"),
		@Mapping(source = "providerInfo.publicIpAsName", target = "publicIpAsName"),
		@Mapping(source = "providerInfo.publicIpAsn", target = "publicIpAsn"),

		@Mapping(source = "networkMobileInfo.networkCountry", target = "networkCountry"),
		@Mapping(source = "networkMobileInfo.networkOperatorMccMnc", target = "networkOperatorMccMnc"),
		@Mapping(source = "networkMobileInfo.networkOperatorName", target = "networkOperatorName"),
		@Mapping(source = "networkMobileInfo.simCountry", target = "simCountry"),
		@Mapping(source = "networkMobileInfo.simOperatorMccMnc", target = "simOperatorMccMnc"),
		@Mapping(source = "networkMobileInfo.simOperatorName", target = "simOperatorName"),

		@Mapping(source = "networkWifiInfo.initialSsid", target = "ssid"),
		@Mapping(source = "networkWifiInfo.initialBssid", target = "bssid"),
		@Mapping(source = "networkWifiInfo.frequency", target = "frequency"),

		@Mapping(source="networkMobileInfo.roaming", target="roaming"),
		@Mapping(source="networkMobileInfo.roamingType", target="roamingType"),
	})
	NetworkPointInTimeInfoDto map(NetworkPointInTime npit);

	@InheritConfiguration
	@Mappings({
		@Mapping(source = "frequency", target="mobileFrequency")
	})
	ComputedNetworkPointInTimeInfoDto map(ComputedNetworkPointInTime cnpit);

	default String map(MccMnc mccMnc) {
		if (mccMnc == null) {
			return null;
		}

		Integer mcc = mccMnc.getMcc();
		Integer mnc = mccMnc.getMnc();

		if (mcc == null && mnc == null) {
			return null;
		}

		return mcc + "-" + mnc;
	}
	
	default Map<MeasurementTypeDto, FullSubMeasurement> parseSubMeasurementMap(Map<MeasurementTypeDto, SubMeasurement> measurementParam) {
		final Map<MeasurementTypeDto, FullSubMeasurement> ret = new HashMap<>();
		for (MeasurementTypeDto type : measurementParam.keySet()) {
			switch (type) {
			case SPEED:
				ret.put(type, this.map((SpeedMeasurement) measurementParam.get(type)));
				break;
			case QOS:
				//ret.put(type, this.map((QoSMeasurement) measurementParam.get(type))); // TODO: ?
				break;
			}
			
		}
		return ret;
	}
}
