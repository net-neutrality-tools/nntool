package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.result.MeasurementResultNetworkPointInTimeDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkPointInTime;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@Mapper(componentModel = "spring", uses = { DateTimeMapper.class })
public interface MeasurementResultNetworkPointInTimeDtoMapper {
	
	@Mappings({
		@Mapping(source="ssid", target="networkWifiInfo.initialSsid"),
		@Mapping(source="bssid", target="networkWifiInfo.initialBssid"),
		
		@Mapping(source="networkCountry", target="networkMobileInfo.networkCountry"),
		@Mapping(expression="java(toMccMnc(measurementResultNetworkPointInTimeDto.getNetworkOperatorMccMnc()))",
			target="networkMobileInfo.networkOperatorMccMnc"),
		@Mapping(source="networkOperatorName", target="networkMobileInfo.networkOperatorName"),
		
		@Mapping(source="simCountry", target="networkMobileInfo.simCountry"),
		@Mapping(expression="java(toMccMnc(measurementResultNetworkPointInTimeDto.getSimOperatorMccMnc()))",
		target="networkMobileInfo.simOperatorMccMnc"),
		@Mapping(source="simOperatorName", target="networkMobileInfo.simOperatorName"),
		@Mapping(source="networkTypeId", target="networkType.networkTypeId")
	})
	NetworkPointInTime map(MeasurementResultNetworkPointInTimeDto arg0);

	default MccMnc toMccMnc(String arg0) {
		if (arg0 == null) {
			return null;
		}
		
		final MccMnc mccMnc = new MccMnc();
		final String[] items = arg0.split("-");
		if (items == null || items.length != 2) {
			return null;
		}
		
		try {
			mccMnc.setMcc(Integer.valueOf(items[0]));
			mccMnc.setMnc(Integer.valueOf(items[1]));
		}
		catch (final Exception e) {
			return null;
		}
		
		return mccMnc;
	}
	
}
