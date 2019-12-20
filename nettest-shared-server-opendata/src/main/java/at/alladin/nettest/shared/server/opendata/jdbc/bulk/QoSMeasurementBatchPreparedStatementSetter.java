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
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;
import at.alladin.nettest.shared.server.opendata.jdbc.QoSMeasurementPreparedStatementSetter;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class QoSMeasurementBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

	private final List<OpenDataFullQoSMeasurement> qosMeasurements;
	
	public QoSMeasurementBatchPreparedStatementSetter(List<FullMeasurementResponse> measurements) {
		this.qosMeasurements = measurements.stream()
			.filter(m -> {
				return 	m.getMeasurements() != null && 
						m.getMeasurements().get(MeasurementTypeDto.QOS) != null &&
						m.getMeasurements().get(MeasurementTypeDto.QOS) instanceof FullQoSMeasurement;
			})
			.map(m -> {
				return new OpenDataFullQoSMeasurement(
					(FullQoSMeasurement) m.getMeasurements().get(MeasurementTypeDto.QOS), 
					m.getOpenDataUuid()
				);
			})
			.collect(Collectors.toList());
	}

	@Override
	public void setValues(PreparedStatement ps, int i) throws SQLException {
		final OpenDataFullQoSMeasurement qosMeasurement = qosMeasurements.get(i);
		
		new QoSMeasurementPreparedStatementSetter(qosMeasurement.getQosMeasurement(), qosMeasurement.getOpenDataUuid()).setValues(ps);
	}

	@Override
	public int getBatchSize() {
		return qosMeasurements.size();
	}
	
	private static class OpenDataFullQoSMeasurement {
		private final FullQoSMeasurement qosMeasurement;
		private final String openDataUuid;
		
		public OpenDataFullQoSMeasurement(FullQoSMeasurement qosMeasurement, String openDataUuid) {
			this.qosMeasurement = qosMeasurement;
			this.openDataUuid = openDataUuid;
		}
		
		public FullQoSMeasurement getQosMeasurement() {
			return qosMeasurement;
		}
		
		public String getOpenDataUuid() {
			return openDataUuid;
		}
	}
}
