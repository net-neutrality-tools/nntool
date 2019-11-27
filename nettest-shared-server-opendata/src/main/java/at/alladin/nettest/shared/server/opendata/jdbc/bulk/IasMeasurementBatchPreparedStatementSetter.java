package at.alladin.nettest.shared.server.opendata.jdbc.bulk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.MeasurementTypeDto;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullMeasurementResponse;
import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullSpeedMeasurement;
import at.alladin.nettest.shared.server.opendata.jdbc.IasMeasurementPreparedStatementSetter;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class IasMeasurementBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

	private final List<OpenDataFullIasMeasurement> IasMeasurements;
	
	public IasMeasurementBatchPreparedStatementSetter(List<FullMeasurementResponse> measurements) {
		this.IasMeasurements = measurements.stream()
			.filter(m -> {
				return 	m.getMeasurements() != null && 
						m.getMeasurements().get(MeasurementTypeDto.SPEED) != null &&
						m.getMeasurements().get(MeasurementTypeDto.SPEED) instanceof FullSpeedMeasurement;
			})
			.map(m -> {
				return new OpenDataFullIasMeasurement(
					(FullSpeedMeasurement) m.getMeasurements().get(MeasurementTypeDto.SPEED), 
					m.getOpenDataUuid()
				);
			})
			.collect(Collectors.toList());
	}

	@Override
	public void setValues(PreparedStatement ps, int i) throws SQLException {
		final OpenDataFullIasMeasurement IasMeasurement = IasMeasurements.get(i);
		
		new IasMeasurementPreparedStatementSetter(IasMeasurement.getIasMeasurement(), IasMeasurement.getOpenDataUuid()).setValues(ps);
	}

	@Override
	public int getBatchSize() {
		return IasMeasurements.size();
	}
	
	private static class OpenDataFullIasMeasurement {
		private final FullSpeedMeasurement iasMeasurement;
		private final String openDataUuid;
		
		public OpenDataFullIasMeasurement(FullSpeedMeasurement iasMeasurement, String openDataUuid) {
			this.iasMeasurement = iasMeasurement;
			this.openDataUuid = openDataUuid;
		}
		
		public FullSpeedMeasurement getIasMeasurement() {
			return iasMeasurement;
		}
		
		public String getOpenDataUuid() {
			return openDataUuid;
		}
	}
}
