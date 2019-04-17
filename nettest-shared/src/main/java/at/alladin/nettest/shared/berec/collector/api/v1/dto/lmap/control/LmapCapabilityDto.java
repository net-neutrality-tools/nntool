package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Agent capabilities including a list of supported Tasks.
 * @author fk
 *
 */
@io.swagger.annotations.ApiModel(description = "Agent capabilities including a list of supported Tasks.")
@JsonClassDescription("Agent capabilities including a list of supported Tasks.")
@JsonInclude(Include.NON_EMPTY)
public class LmapCapabilityDto {
	
	/**
	 * A short description of the software implementing the Measurement Agent. 
	 * This should include the version number of the Measurement Agent software.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "A short description of the software implementing the Measurement Agent. This should include the version number of the Measurement Agent software.")
	@JsonPropertyDescription("A short description of the software implementing the Measurement Agent. This should include the version number of the Measurement Agent software.")
	@Expose
	@SerializedName("version")
	@JsonProperty(required = true, value = "version")
	private String version;
	
	/**
	 * An optional unordered set of tags that provide additional information about the capabilities of the Measurement Agent.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "An optional unordered set of tags that provide additional information about the capabilities of the Measurement Agent.")
	@JsonPropertyDescription("An optional unordered set of tags that provide additional information about the capabilities of the Measurement Agent.")
	@Expose
	@SerializedName("tag")
	@JsonProperty(required = true, value = "tag")
	private List<String> tag = new ArrayList<>(); // TODO: rename tags
	
	/**
	 * A list of Tasks that the Measurement Agent supports.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "A list of Tasks that the Measurement Agent supports.")
	@JsonPropertyDescription("A list of Tasks that the Measurement Agent supports.")
	@Expose
	@SerializedName("tasks")
	@JsonProperty(required = true, value = "tasks")
	private List<LmapCapabilityTaskDto> tasks = new ArrayList<>();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getTag() {
		return tag;
	}

	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	public List<LmapCapabilityTaskDto> getTasks() {
		return tasks;
	}

	public void setTasks(List<LmapCapabilityTaskDto> tasks) {
		this.tasks = tasks;
	}
}
