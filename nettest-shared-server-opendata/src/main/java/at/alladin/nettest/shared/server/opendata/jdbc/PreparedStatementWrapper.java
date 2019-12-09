package at.alladin.nettest.shared.server.opendata.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.function.Supplier;

import org.joda.time.LocalDateTime;

/**
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
public class PreparedStatementWrapper {

	final PreparedStatement preparedStatement;
	
	public PreparedStatementWrapper(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}
	
	public void setObject(int parameterIndex, Object x) throws SQLException {
		preparedStatement.setObject(parameterIndex, x);
	}
	
	public void setString(int parameterIndex, String x) throws SQLException {
		preparedStatement.setString(parameterIndex, x);
	}
	
	public void setInt(int parameterIndex, int x) throws SQLException {
		preparedStatement.setInt(parameterIndex, x);
	}
	
	public void setLong(int parameterIndex, long x) throws SQLException {
		preparedStatement.setLong(parameterIndex, x);
	}
	
	public void setDouble(int parameterIndex, double x) throws SQLException {
		preparedStatement.setDouble(parameterIndex, x);
	}
	
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		preparedStatement.setBoolean(parameterIndex, x);
	}
	
	////
	
	public void setAsUuid(int parameterIndex, String uuidString) throws SQLException {
		UUID uuid = null;
		
		try {
			uuid = UUID.fromString(uuidString);
		} catch (Exception ex) {

		}
		
		preparedStatement.setObject(parameterIndex, uuid);
	}
	
	public void setJodaLocalDateTime(int parameterIndex, LocalDateTime ldt) throws SQLException {
		if (ldt == null) {
			preparedStatement.setObject(parameterIndex, null);
		} else {
			preparedStatement.setObject(parameterIndex, new Timestamp(ldt.toDateTime().getMillis())); 
		}
	}
	
	public void setObject(int parameterIndex, Supplier<Object> func) throws SQLException {
		Object s = null;
		
		try {
			s = func.get();
		} catch (Exception ex) {
			
		}
		
		preparedStatement.setObject(parameterIndex, s);
	}
	
	public void setString(int parameterIndex, Supplier<String> func) throws SQLException {
		String s = null;
		
		try {
			s = func.get();
		} catch (Exception ex) {
			
		}
		
		preparedStatement.setString(parameterIndex, s);
	}
	
	public void setInt(int parameterIndex, Supplier<Integer> func) throws SQLException {
		try {
			final Integer s = func.get();
			if (s != null) {
				preparedStatement.setInt(parameterIndex, s);
				return;
			}
		} catch (Exception ex) {
			
		}
		
		preparedStatement.setObject(parameterIndex, null);
	}
	
	public void setLong(int parameterIndex, Supplier<Long> func) throws SQLException {
		try {
			final Long s = func.get();
			if (s != null) {
				preparedStatement.setLong(parameterIndex, s);
				return;
			}
		} catch (Exception ex) {
			
		}
		
		preparedStatement.setObject(parameterIndex, null);
	}
	
	public void setDouble(int parameterIndex, Supplier<Double> func) throws SQLException {
		try {
			final Double s = func.get();
			if (s != null) {
				preparedStatement.setDouble(parameterIndex, s);
				return;
			}
		} catch (Exception ex) {

		}
		
		preparedStatement.setObject(parameterIndex, null);
	}
	
	public void setBoolean(int parameterIndex, Supplier<Boolean> func) throws SQLException {
		try {
			final Boolean s = func.get();
			if (s != null) {
				preparedStatement.setBoolean(parameterIndex, s);
				return;
			}
		} catch (Exception ex) {
			
		}
		
		preparedStatement.setObject(parameterIndex, null);
	}
}
