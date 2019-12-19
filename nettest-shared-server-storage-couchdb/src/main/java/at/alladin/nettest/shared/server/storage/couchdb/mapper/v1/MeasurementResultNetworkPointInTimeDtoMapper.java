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
