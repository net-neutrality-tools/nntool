package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author fk
 */
public abstract class LmapEventTypeDto {
	
	/**
	 * Type identifier of the given event.
	 */
	@io.swagger.annotations.ApiModelProperty(required = true, value = "Type identifier of the given event.")
	@JsonPropertyDescription("Type identifier of the given event.")
	@Expose
	@SerializedName("type")
	@JsonProperty(required = true, value = "type")
	protected EventTypeEnum type;
	
	public LmapEventTypeDto (final EventTypeEnum type) {
		this.type = type;
	}
	
	public static enum EventTypeEnum {
		IMMEDIATE
	}

}
