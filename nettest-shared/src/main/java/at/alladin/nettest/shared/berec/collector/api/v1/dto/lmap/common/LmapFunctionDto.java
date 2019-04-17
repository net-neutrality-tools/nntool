package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.common;

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
 * This grouping models a list of entries in a registry that identify functions of a Task.
 * @author fk
 *
 */
@io.swagger.annotations.ApiModel(description = "This grouping models a list of entries in a registry that identify functions of a Task.")
@JsonClassDescription("This grouping models a list of entries in a registry that identify functions of a Task.")
@JsonInclude(Include.NON_EMPTY)
public class LmapFunctionDto {
	
	/**
	 * The unique name of a Task.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The unique name of a Task.")
	@JsonPropertyDescription("The unique name of a Task.")
	@Expose
	@SerializedName("uri")
	@JsonProperty(required = true, value = "uri")
	private String uri;
	
	/**
	 * A set of roles for the identified registry entry.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "A set of roles for the identified registry entry.")
	@JsonPropertyDescription("A set of roles for the identified registry entry.")
	@Expose
	@SerializedName("role")
	@JsonProperty(required = true, value = "role")
	private List<String> roleList = new ArrayList<>(); // TODO: rename roles

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}
}
