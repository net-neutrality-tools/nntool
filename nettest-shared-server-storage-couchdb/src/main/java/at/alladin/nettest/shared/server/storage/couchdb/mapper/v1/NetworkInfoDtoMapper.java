package at.alladin.nettest.shared.server.storage.couchdb.mapper.v1;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.MccMnc;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkInfo;
import at.alladin.nettest.shared.server.storage.couchdb.domain.model.NetworkPointInTime;

/**
 * 
 * @author alladin-IT GmbH (fk@alladin.at)
 *
 */
@Mapper(componentModel = "spring", uses = { DateTimeMapper.class })
public interface NetworkInfoDtoMapper {

	@Mappings({
		@Mapping(source="networkPointsInTime", target="networkPointInTimeInfo"),
		@Mapping(source="signalInfo.signals", target="signals")
	})
	NetworkInfoDto map(NetworkInfo networkInfo);
	
	@Mappings({
		@Mapping(source="clientPublicIp", target="agentPublicIp"),
		@Mapping(source="clientPrivateIp", target="agentPrivateIp"),
		@Mapping(source="clientPublicIpCountryCode", target="agentPublicIpCountryCode"),
		@Mapping(source="networkType.networkTypeId", target="networkTypeId"),
		@Mapping(source="networkType.name", target="networkTypeName"),
		@Mapping(source="networkType.groupName", target="networkTypeGroupName"),
		@Mapping(source="networkType.category", target="networkTypeCategory"),
		@Mapping(source="providerInfo.publicIpAsn", target="publicIpAsn"),
		@Mapping(source="providerInfo.publicIpAsName", target="publicIpAsName"),
		@Mapping(source="providerInfo.countryCodeAsn", target="countryCodeAsn"),
		@Mapping(source="providerInfo.provider.name", target="providerName"),
		@Mapping(source="providerInfo.provider.shortName", target="providerShortName"),
		@Mapping(source="networkWifiInfo.initialSsid", target="ssid"),
		@Mapping(source="networkWifiInfo.initialBssid", target="bssid"),
		@Mapping(source="networkMobileInfo.networkCountry", target="networkCountry"),
		@Mapping(source="networkMobileInfo.networkOperatorMccMnc", target="networkOperatorMccMnc"),
		@Mapping(source="networkMobileInfo.networkOperatorName", target="networkOperatorName"),
		@Mapping(source="networkMobileInfo.simCountry", target="simCountry"),
		@Mapping(source="networkMobileInfo.simOperatorMccMnc", target="simOperatorMccMnc"),
		@Mapping(source="networkMobileInfo.simOperatorName", target="simOperatorName"),
		@Mapping(source="networkMobileInfo.roaming", target="roaming"),
		@Mapping(source="networkMobileInfo.roamingType", target="roamingType"),
	})
	NetworkPointInTimeInfoDto map(NetworkPointInTime arg);
	
	default String map(MccMnc mccMnc) {
		return mccMnc != null ? mccMnc.toString() : null;
	}
}
