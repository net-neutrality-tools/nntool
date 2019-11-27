package at.alladin.nettest.shared.server.opendata.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.full.FullQoSMeasurement;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class QoSMeasurementPreparedStatementSetter extends SubMeasurementPreparedStatementSetter {

	private final FullQoSMeasurement qosMeasurement;
	
	public QoSMeasurementPreparedStatementSetter(FullQoSMeasurement qosMeasurement, String openDataUuid) {
		super(qosMeasurement, openDataUuid);
		
		this.qosMeasurement = qosMeasurement;
	}

	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		super.setValues(ps);
		
		// TODO: qos_measurement_results and qos_objectives
	}
}
