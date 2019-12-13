package at.alladin.nettest.service.loadbalancer.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author lb@alladin.at
 *
 */
public class LoadApiAllServersReport {

	@JsonProperty("report_list")
	List<LoadApiReport> reportList;

	public List<LoadApiReport> getReportList() {
		return reportList;
	}

	public void setReportList(List<LoadApiReport> reportList) {
		this.reportList = reportList;
	}

	@Override
	public String toString() {
		return "LoadApiAllServersReport [reportList=" + reportList + "]";
	}
}
