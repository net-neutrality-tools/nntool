package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class holds LMAP specific result values (see LMAP report result model, RFC 8194) 
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 * 
 */
@JsonClassDescription("This class holds LMAP specific result values (see LMAP report result model)")
public class SubMeasurementLmapResult {

	/**
	 * The name of the Schedule that produced the result.
	 * <br>
	 * References RFC 8194: lmap-report.result.schedule
	 */
	@JsonPropertyDescription("The name of the Schedule that produced the result. References RFC 8194: lmap-report.result.schedule")
	@Expose
	@SerializedName("schedule")
	@JsonProperty(value = "schedule")
	private String schedule;

	/**
	 * The name of the Action in the Schedule that produced the result.
	 * <br>
	 * References RFC 8194: lmap-report.result.action
	 */
	@JsonPropertyDescription("The name of the Action in the Schedule that produced the result. References RFC 8194: lmap-report.result.action")
	@Expose
	@SerializedName("action")
	@JsonProperty(value = "action")
	private String action;

	/**
	 * The name of the Task that produced the result.
	 * <br>
	 * References RFC 8194: lmap-report.result.task
	 */
	@JsonPropertyDescription("The name of the Task that produced the result. References RFC 8194: lmap-report.result.task")
	@Expose
	@SerializedName("task")
	@JsonProperty(value = "task")
	private String task;
	
	/**
	 * Collection of all LMAP specific tags. This is the joined set of tags defined for the Task object, the Schedule object, and the Action object.
	 * <br>
	 * References RFC 8194: lmap-report.result.tag
	 */
	@JsonPropertyDescription("Collection of all LMAP specific tags. This is the joined set of tags defined for the Task object, the Schedule object, and the Action object. References RFC 8194: lmap-report.result.tag")
	@Expose
	@SerializedName("tags")
	@JsonProperty("tags")
	private List<String> tags;
	
	/**
	 * The date and time of the event that triggered the Schedule of the Action that produced the reported result values. 
	 * The date and time does not include any added randomization.
	 * <br>
	 * References RFC 8194: lmap-report.result.event
	 */
	@JsonPropertyDescription("The date and time of the event that triggered the Schedule of the Action that produced the reported result values. The date and time does not include any added randomization. References RFC 8194: lmap-report.result.event")
	@Expose
	@SerializedName("event_time")
	@JsonProperty(value = "event_time")
	private LocalDateTime eventTime;
	
	/**
	 * The status code returned by the execution of this SubMeasurement/Action.
	 * <br>
	 * References RFC 8194: lmap-report.result.status
	 */
	@JsonPropertyDescription("The status code returned by the execution of this SubMeasurement/Action. References RFC 8194: lmap-report.result.status")
	@Expose
	@SerializedName("status_code")
	@JsonProperty(required = true, value = "status_code")
	private Integer statusCode;

	public String getSchedule() {
		return schedule;
	}

	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
}
