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

package at.alladin.nettest.shared.server.opendata.jdbc.bulk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.server.opendata.jdbc.MeasurementPreparedStatementSetter;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class MeasurementBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

	private final List<FullMeasurementResponse> measurements;
	
	public MeasurementBatchPreparedStatementSetter(List<FullMeasurementResponse> measurements) {
		this.measurements = measurements;
	}

	@Override
	public void setValues(PreparedStatement ps, int i) throws SQLException {
		final FullMeasurementResponse measurement = measurements.get(i);
		
		new MeasurementPreparedStatementSetter(measurement).setValues(ps);
	}

	@Override
	public int getBatchSize() {
		return measurements.size();
	}
}
