package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author fk
 */
@JsonInclude(Include.NON_EMPTY)
public class LmapStopEndDto extends LmapStopDto {
	
	/**
	 * The event source controlling the graceful forced termination of the scheduled Actions.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The event source controlling the graceful forced termination of the scheduled Actions.")
	@JsonPropertyDescription("The event source controlling the graceful forced termination of the scheduled Actions.")
	@Expose
	@SerializedName("end")
	@JsonProperty(required = true, value = "end")
	private String end;

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
}
