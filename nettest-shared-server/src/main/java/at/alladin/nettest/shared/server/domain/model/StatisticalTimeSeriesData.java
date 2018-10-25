package at.alladin.nettest.shared.server.domain.model;

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
	}
}
