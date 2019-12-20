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

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.shared.FullRttInfoDto;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class IasMeasurementPreparedStatementSetter extends SubMeasurementPreparedStatementSetter {

	private final FullSpeedMeasurement iasMeasurement;
	
	public IasMeasurementPreparedStatementSetter(FullSpeedMeasurement iasMeasurement, String openDataUuid) {
		super(iasMeasurement, openDataUuid);
		
		this.iasMeasurement = iasMeasurement;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		super.setValues(ps);
		
		final FullRttInfoDto rttInfo = iasMeasurement.getRttInfo();
		
		final PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(ps);
		
		psWrapper.setLong(12, iasMeasurement::getThroughputAvgDownloadBps);
		psWrapper.setLong(13, iasMeasurement::getThroughputAvgUploadBps);
		psWrapper.setDouble(14, iasMeasurement::getThroughputAvgDownloadLog);
		psWrapper.setDouble(15, iasMeasurement::getThroughputAvgUploadLog);
		psWrapper.setLong(16, iasMeasurement::getBytesDownload);
		psWrapper.setLong(17, iasMeasurement::getBytesUpload);
		psWrapper.setLong(18, iasMeasurement::getRequestedDurationDownloadNs);
		psWrapper.setLong(19, iasMeasurement::getRequestedDurationUploadNs);
		psWrapper.setLong(20, iasMeasurement::getDurationNs);
		psWrapper.setLong(21, iasMeasurement::getDurationUploadNs);
		psWrapper.setLong(22, iasMeasurement::getRelativeStartTimeDownloadNs);
		psWrapper.setLong(23, iasMeasurement::getRelativeStartTimeUploadNs);
		psWrapper.setLong(24, iasMeasurement::getDurationRttNs);
		ps.setObject(25, null /*iasMeasurement.getConnectionInfo()*/);
		ps.setObject(28, null /*iasMeasurement.getDownloadRawData()*/); // TODO: upload
		ps.setObject(29, null /*iasMeasurement.getRttInfo()*/);
		ps.setObject(30, null);
		ps.setObject(31, null);
		
		if (rttInfo != null) {
			psWrapper.setLong(26, rttInfo::getMedianNs);
			psWrapper.setDouble(27, rttInfo::getMedianLog);
		} else {
			ps.setObject(26, null);
			ps.setObject(27, null);
		}
	}
}
