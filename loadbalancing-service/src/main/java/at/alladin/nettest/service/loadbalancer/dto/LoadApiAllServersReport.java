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
