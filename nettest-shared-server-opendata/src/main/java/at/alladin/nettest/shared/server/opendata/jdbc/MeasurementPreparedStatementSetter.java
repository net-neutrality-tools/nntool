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

package at.alladin.nettest.shared.server.opendata.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.ComputedNetworkPointInTimeInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.DeviceInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.GeoLocationDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.MeasurementAgentInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.NetworkInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.OperatingSystemInfoDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.SignalDto;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class MeasurementPreparedStatementSetter implements PreparedStatementSetter {

	private final FullMeasurementResponse measurementDto;
	
	public MeasurementPreparedStatementSetter(FullMeasurementResponse measurementDto) {
		this.measurementDto = measurementDto;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		final DeviceInfoDto deviceInfo = measurementDto.getDeviceInfo();
		final OperatingSystemInfoDto osInfo = deviceInfo.getOsInfo();
		final NetworkInfoDto networkInfo = measurementDto.getNetworkInfo();
		final ComputedNetworkPointInTimeInfoDto computedNetworkInfo = measurementDto.getComputedNetworkInfo();
		final MeasurementAgentInfoDto agentInfo = measurementDto.getAgentInfo();
		
		final PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(ps);
		
		psWrapper.setAsUuid(1, measurementDto.getOpenDataUuid());
		psWrapper.setAsUuid(2, agentInfo.getUuid());
		psWrapper.setAsUuid(3, measurementDto.getSystemUuid());
		
		psWrapper.setJodaLocalDateTime(4, measurementDto.getStartTime());
		psWrapper.setJodaLocalDateTime(5, measurementDto.getEndTime());
		
		psWrapper.setLong(6, measurementDto::getDurationNs);
		psWrapper.setString(7, osInfo::getName);
		psWrapper.setString(8, osInfo::getVersion);
		psWrapper.setString(9, osInfo::getApiLevel);
		psWrapper.setObject(10, null); //osInfo.getCpu*
		psWrapper.setObject(11, null); //osInfo.getMemory*
		psWrapper.setString(12, deviceInfo::getCodeName);
		psWrapper.setString(13, deviceInfo::getModel);
		psWrapper.setString(14, deviceInfo::getFullName);
		
		psWrapper.setString(22, agentInfo::getAppVersionName);
		psWrapper.setInt(23, agentInfo::getAppVersionCode);
		psWrapper.setString(24, agentInfo::getLanguage);
		psWrapper.setString(25, agentInfo::getAppGitRevision);
		psWrapper.setString(26, agentInfo::getTimezone);
		
		ps.setObject(36, null); // ?
		ps.setObject(37, null); // ?
		
		psWrapper.setString(48, () -> { return agentInfo.getType().name(); });
		psWrapper.setString(54, measurementDto::getTag);
		
		if (computedNetworkInfo != null) {
			psWrapper.setObject(15, computedNetworkInfo::getAgentPublicIp);
			psWrapper.setLong(16, computedNetworkInfo::getPublicIpAsn);
			psWrapper.setString(17, computedNetworkInfo::getPublicIpRdns);
			psWrapper.setString(18, computedNetworkInfo::getPublicIpAsName);
			psWrapper.setString(19, computedNetworkInfo::getCountryCodeAsn);
			psWrapper.setString(20, computedNetworkInfo::getProviderName);
			psWrapper.setString(21, computedNetworkInfo::getProviderShortName);
			psWrapper.setInt(28, () -> { return Integer.parseInt(computedNetworkInfo.getNetworkOperatorMccMnc().split("-")[0]); });
			psWrapper.setInt(29, () -> { return Integer.parseInt(computedNetworkInfo.getNetworkOperatorMccMnc().split("-")[1]); });
			psWrapper.setString(30, computedNetworkInfo::getNetworkCountry);
			psWrapper.setString(31, computedNetworkInfo::getNetworkOperatorName);
			psWrapper.setInt(32, () -> { return Integer.parseInt(computedNetworkInfo.getSimOperatorMccMnc().split("-")[0]); });
			psWrapper.setInt(33, () -> { return Integer.parseInt(computedNetworkInfo.getSimOperatorMccMnc().split("-")[1]); });
			psWrapper.setString(34, computedNetworkInfo::getSimOperatorName);
			psWrapper.setString(35, computedNetworkInfo::getSimCountry);
			
			psWrapper.setInt(38, computedNetworkInfo::getNetworkTypeId);
			psWrapper.setString(46, computedNetworkInfo::getNetworkTypeGroupName);
			psWrapper.setString(47, computedNetworkInfo::getAgentPublicIpCountryCode);
		} else {
			ps.setObject(15, null);
			ps.setObject(16, null);
			ps.setObject(17, null);
			ps.setObject(18, null);
			ps.setObject(19, null);
			ps.setObject(20, null);
			ps.setObject(21, null);
			ps.setObject(28, null);
			ps.setObject(29, null);
			ps.setObject(30, null);
			ps.setObject(31, null);
			ps.setObject(32, null);
			ps.setObject(33, null);
			ps.setObject(34, null);
			ps.setObject(35, null);
			ps.setObject(38, null);
			ps.setObject(46, null);
			ps.setObject(47, null);
		}
		
		// TODO: COMPUTED SIGNALS
		final List<SignalDto> signalList = networkInfo.getSignals();
		if (signalList != null && signalList.size() > 0) {
			final SignalDto signalInfo = signalList.get(0);
			
			ps.setObject(27, null); //networkInfo.getSignals()
			
			psWrapper.setInt(40, signalInfo::getSignalStrength2g3gDbm);
			psWrapper.setInt(41, signalInfo::getLteRsrpDbm);
			psWrapper.setInt(42, signalInfo::getLteRsrqDb);
			psWrapper.setInt(43, signalInfo::getLteRssnrDb);
			psWrapper.setInt(44, signalInfo::getWifiLinkSpeedBps);
			psWrapper.setInt(45, signalInfo::getWifiRssiDbm);
			psWrapper.setObject(53, () -> { return signalInfo.getCellInfo().getFrequency(); });
			psWrapper.setString(55, signalInfo::getWifiBssid);
			psWrapper.setString(56, signalInfo::getWifiSsid);
		} else {
			ps.setObject(27, null);
			ps.setObject(40, null);
			ps.setObject(41, null);
			ps.setObject(42, null);
			ps.setObject(43, null);
			ps.setObject(44, null);
			ps.setObject(45, null);
			ps.setObject(53, null);
			ps.setObject(55, null);
			ps.setObject(56, null);
		}
		
		final List<GeoLocationDto> geoLocationList = measurementDto.getGeoLocations();
		if (geoLocationList != null && geoLocationList.size() > 0) {
			final GeoLocationDto geoLocation = geoLocationList.get(0);
			
			psWrapper.setDouble(39, geoLocation::getAccuracy);
			psWrapper.setDouble(49, geoLocation::getLatitude);
			psWrapper.setDouble(50, geoLocation::getLongitude);
			psWrapper.setDouble(51, geoLocation::getLongitude);
			psWrapper.setDouble(52, geoLocation::getLatitude);
		} else {
			ps.setObject(39, null);
			ps.setObject(49, null);
			ps.setObject(50, null);
			ps.setObject(51, null);
			ps.setObject(52, null);
		}
	}
}
