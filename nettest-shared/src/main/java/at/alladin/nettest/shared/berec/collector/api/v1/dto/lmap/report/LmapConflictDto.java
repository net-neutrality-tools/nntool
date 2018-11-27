package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.report;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The names of Tasks overlapping with the execution of the Task that has produced this result.
 * @author lb
 *
 */
@io.swagger.annotations.ApiModel(description = "The names of Tasks overlapping with the execution of the Task that has produced this result.")
@JsonClassDescription("The names of Tasks overlapping with the execution of the Task that has produced this result.")
@JsonInclude(Include.NON_EMPTY)
public class LmapConflictDto {

	/**
	 * The names of Tasks overlapping with the execution of the Task that has produced this result.
	 * 
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "The names of Tasks overlapping with the execution of the Task that has produced this result.")
	@JsonPropertyDescription("The names of Tasks overlapping with the execution of the Task that has produced this result.")
	@Expose
	@SerializedName("schedule-name")
	@JsonProperty(required = false, value = "schedule-name")	
	private String scheduleName;
	
	/**
	 * The name of an Action within the Schedule that might have impacted the execution of the Task 
	 * that has produced this result.
	 * 
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "The name of an Action within the Schedule that might have impacted the execution of the Task that has produced this result.")
	@JsonPropertyDescription("The name of an Action within the Schedule that might have impacted the execution of the Task that has produced this result.")
	@Expose
	@SerializedName("action-name")
	@JsonProperty(required = false, value = "action-name")	
	private String actionName;
	
	/**
	 * The name of the Task executed by an Action within the Schedule that might have impacted 
	 * the execution of the Task that has produced this result.
	 * 
	 */
	@io.swagger.annotations.ApiModelProperty(required = false, value = "The name of the Task executed by an Action within the Schedule that might have impacted the execution of the Task that has produced this result.")
	@JsonPropertyDescription("The name of the Task executed by an Action within the Schedule that might have impacted the execution of the Task that has produced this result.")
	@Expose
	@SerializedName("task-name")
	@JsonProperty(required = false, value = "task-name")	
	private String taskName;

}
