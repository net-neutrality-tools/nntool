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

package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class stores raw values of type T as well as common statistical values which are calculated from the raw values.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 * @param <T> Type of the raw values
 */
@JsonClassDescription("This class stores raw values of type T as well as common statistical values which are calculated from the raw values.")
public class StatisticalTimeSeriesData<T> {

	/**
	 * The raw values with relative time and value which are used to calculate the statistics.
	 */
	@JsonPropertyDescription("The raw values with relative time and value which are used to calculate the statistics.")
	@Expose
	@SerializedName("raw_values")
	@JsonProperty("raw_values")
	private List<PointInRelativeTimeValue<T>> rawValues;

	/**
	 * Minimum value of data from rawValues.
	 */
	@JsonPropertyDescription("Minimum value of data from rawValues.")
	@Expose
	@SerializedName("min")
	@JsonProperty("min")
	private Double min;

	/**
	 * Maximum value of data from rawValues.
	 */
	@JsonPropertyDescription("Maximum value of data from rawValues.")
	@Expose
	@SerializedName("max")
	@JsonProperty("max")
	private Double max;

	/**
	 * Average of data from rawValues.
	 */
	@JsonPropertyDescription("Average of data from rawValues.")
	@Expose
	@SerializedName("average")
	@JsonProperty("average")
	private Long average;

	/**
	 * Median of data from rawValues.
	 */
	@JsonPropertyDescription("Median of data from rawValues.")
	@Expose
	@SerializedName("median")
	@JsonProperty("median")
	private Long median;

	/**
	 * Variance of data from rawValues.
	 */
	@JsonPropertyDescription("Variance of data from rawValues.")
	@Expose
	@SerializedName("variance")
	@JsonProperty("variance")
	private Double variance;

	/**
	 * Calculated standard deviation.
	 */
	@JsonPropertyDescription("Calculated standard deviation.")
	@Expose
	@SerializedName("standard_deviation")
	@JsonProperty("standard_deviation")
	private Long standardDeviation;
	
	public List<PointInRelativeTimeValue<T>> getRawValues() {
		return rawValues;
	}

	public void setRawValues(List<PointInRelativeTimeValue<T>> rawValues) {
		this.rawValues = rawValues;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Long getAverage() {
		return average;
	}

	public void setAverage(Long average) {
		this.average = average;
	}

	public Long getMedian() {
		return median;
	}

	public void setMedian(Long median) {
		this.median = median;
	}

	public Double getVariance() {
		return variance;
	}

	public void setVariance(Double variance) {
		this.variance = variance;
	}

	public Long getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(Long standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	/**
	 * Holds a value from a point in time.
	 *
	 * @author alladin-IT GmbH (lb@alladin.at)
	 * @author alladin-IT GmbH (bp@alladin.at)
	 *
	 * @param <T> Type of the value object
	 */
	@JsonClassDescription("Holds a value from a point in time.")
	public static class PointInRelativeTimeValue<T> {

		/**
		 * The relative time in nanoseconds to the measurement start.
		 */
		@JsonPropertyDescription("The relative time in nanoseconds to the measurement start.")
		@Expose
		@SerializedName("relative_time_ns")
		@JsonProperty("relative_time_ns")
		private Long relativeTimeNs;

		/**
		 * The value recorded at this point in time.
		 */
		@JsonPropertyDescription("The value recorded at this point in time.")
		@Expose
		@SerializedName("value")
		@JsonProperty("value")
		private T value;

		public Long getRelativeTimeNs() {
			return relativeTimeNs;
		}

		public void setRelativeTimeNs(Long relativeTimeNs) {
			this.relativeTimeNs = relativeTimeNs;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}
}
