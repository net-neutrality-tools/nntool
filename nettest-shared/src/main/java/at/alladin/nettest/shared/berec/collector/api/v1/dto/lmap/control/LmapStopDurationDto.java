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
public class LmapStopDurationDto extends LmapStopDto {
	
	/**
	 * The duration controlling the graceful forced termination of the scheduled Actions, in ms.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "The duration controlling the graceful forced termination of the scheduled Actions, in ms.")
	@JsonPropertyDescription("The duration controlling the graceful forced termination of the scheduled Actions, in ms.")
	@Expose
	@SerializedName("duration")
	@JsonProperty(required = true, value = "duration")
	private Integer duration;

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}
}
