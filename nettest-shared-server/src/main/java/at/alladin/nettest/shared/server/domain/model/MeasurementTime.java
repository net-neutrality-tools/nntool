package at.alladin.nettest.shared.server.domain.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Start and end time stamps and the duration of a (sub-) measurement.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Start and end time stamps and the duration of a (sub-) measurement.")
public class MeasurementTime {

	/**
	 * Start Date and time for this (sub-) measurement. Date and time is always stored as UTC.
	 * Before showing date and time to the user it's converted to the users time zone by the client. 
	 */
	@JsonPropertyDescription("Start Date and time for this (sub-) measurement. Date and time is always stored as UTC. Before showing date and time to the user it's converted to the users time zone by the client.")
	@Expose
	@SerializedName("start_time")
	@JsonProperty("start_time")
	private LocalDateTime startTime;
	
	/**
	 * End Date and time for this (sub-) measurement. Date and time is always stored as UTC.
	 * Before showing date and time to the user it's converted to the users time zone by the client.
	 */
	@JsonPropertyDescription("End Date and time for this (sub-) measurement. Date and time is always stored as UTC. Before showing date and time to the user it's converted to the users time zone by the client.")
	@Expose
	@SerializedName("end_time")
	@JsonProperty("end_time")
	private LocalDateTime endTime;
	
	/**
	 * Duration of a measurement.
	 */
	@JsonPropertyDescription("Duration of a measurement.")
	@Expose
	@SerializedName("duration_ns")
	@JsonProperty("duration_ns")
	private Long durationNs;
}