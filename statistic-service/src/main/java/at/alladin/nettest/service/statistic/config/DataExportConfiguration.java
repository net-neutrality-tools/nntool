package at.alladin.nettest.service.statistic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lb@alladin.at
 *
 */
@ConfigurationProperties(prefix="data-export")
public class DataExportConfiguration {

	private Map<String, String> fields = new HashMap<>();
	
	private String suffix;
	
	private String whereClause;
	
	private String whereOpenTestUuidClause;
	
	private String whereDayClause;
	
	private String whereMonthClause;
	
	private String whereYearClause;

	private String orderBy;
	
	private Long cacheThresholdSec = 3600L;
	
	private boolean compress;
	
	public boolean isCompress() {
		return compress;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Long getCacheThresholdSec() {
		return cacheThresholdSec;
	}

	public void setCacheThresholdSec(Long cacheThresholdSec) {
		this.cacheThresholdSec = cacheThresholdSec;
	}
	
	public String getWhereOpenTestUuidClause() {
		return whereOpenTestUuidClause;
	}

	public void setWhereOpenTestUuidClause(String whereOpenTestUuidClause) {
		this.whereOpenTestUuidClause = whereOpenTestUuidClause;
	}

	public String getWhereDayClause() {
		return whereDayClause;
	}

	public void setWhereDayClause(String whereDayClause) {
		this.whereDayClause = whereDayClause;
	}

	public String getWhereMonthClause() {
		return whereMonthClause;
	}

	public void setWhereMonthClause(String whereMonthClause) {
		this.whereMonthClause = whereMonthClause;
	}

	public String getWhereYearClause() {
		return whereYearClause;
	}

	public void setWhereYearClause(String whereYearClause) {
		this.whereYearClause = whereYearClause;
	}

	@Override
	public String toString() {
		return "DataExportConfiguration [fields=" + fields + ", suffix=" + suffix + ", whereClause=" + whereClause
				+ ", whereOpenTestUuidClause=" + whereOpenTestUuidClause + ", whereDayClause=" + whereDayClause
				+ ", whereMonthClause=" + whereMonthClause + ", whereYearClause=" + whereYearClause + ", orderBy="
				+ orderBy + ", cacheThresholdSec=" + cacheThresholdSec + ", compress=" + compress + "]";
	}
}
