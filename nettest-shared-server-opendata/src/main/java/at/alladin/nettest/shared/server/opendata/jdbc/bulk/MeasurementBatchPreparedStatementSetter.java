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
