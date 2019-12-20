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

package at.alladin.nettest.service.statistic.web.api.v1.dto;

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
