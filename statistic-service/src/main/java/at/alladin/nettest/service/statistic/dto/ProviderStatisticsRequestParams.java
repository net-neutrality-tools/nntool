package at.alladin.nettest.service.statistic.dto;

/**
 * 
 * @author lb
 *
 */
public class ProviderStatisticsRequestParams {
	
	public static enum MeasurementType {
		MOBILE,
		WIFI,
		BROWSER
	}
	
	Long period;

	MeasurementType measurementType;

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public MeasurementType getMeasurementType() {
		return measurementType;
	}

	public void setMeasurementType(MeasurementType measurementType) {
		this.measurementType = measurementType;
	}

	@Override
	public String toString() {
		return "ProviderStatisticsRequestParams [period=" + period + ", measurementType=" + measurementType + "]";
	}
}
