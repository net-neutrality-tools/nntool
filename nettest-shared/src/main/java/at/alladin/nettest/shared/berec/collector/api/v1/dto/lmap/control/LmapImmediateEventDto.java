package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This immediate Event object triggers immediately when it is configured.
 * @author fk
 */
@io.swagger.annotations.ApiModel(description = "This immediate Event object triggers immediately when it is configured.")
@JsonClassDescription("This immediate Event object triggers immediately when it is configured.")
@JsonInclude(Include.NON_EMPTY)
public class LmapImmediateEventDto extends LmapEventTypeDto {
	
	public LmapImmediateEventDto () {
		super(EventTypeEnum.IMMEDIATE);
	}
}
