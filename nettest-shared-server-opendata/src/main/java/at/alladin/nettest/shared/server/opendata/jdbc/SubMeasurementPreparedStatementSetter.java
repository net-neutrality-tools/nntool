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

import org.springframework.jdbc.core.PreparedStatementSetter;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSubMeasurement;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class SubMeasurementPreparedStatementSetter implements PreparedStatementSetter {

	private final FullSubMeasurement subMeasurement;
	private final String openDataUuid;
	
	public SubMeasurementPreparedStatementSetter(FullSubMeasurement subMeasurement, String openDataUuid) {
		this.subMeasurement = subMeasurement;
		this.openDataUuid = openDataUuid;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		final PreparedStatementWrapper psWrapper = new PreparedStatementWrapper(ps);
		
		psWrapper.setAsUuid(1, openDataUuid);

		psWrapper.setLong(2, subMeasurement::getRelativeStartTimeNs);
		psWrapper.setLong(3, subMeasurement::getRelativeEndTimeNs);
		
		psWrapper.setJodaLocalDateTime(4, subMeasurement.getStartTime());
		psWrapper.setJodaLocalDateTime(5, subMeasurement.getEndTime());
		
		psWrapper.setLong(6, subMeasurement::getDurationNs);

		psWrapper.setString(7, () -> { return subMeasurement.getStatus().name(); });
		psWrapper.setString(8, () -> { return subMeasurement.getReason().name(); });
		
		psWrapper.setString(9, subMeasurement::getVersionProtocol);
		psWrapper.setString(10, subMeasurement::getVersionLibrary);
		
		psWrapper.setBoolean(11, subMeasurement.isImplausible());
	}
}
