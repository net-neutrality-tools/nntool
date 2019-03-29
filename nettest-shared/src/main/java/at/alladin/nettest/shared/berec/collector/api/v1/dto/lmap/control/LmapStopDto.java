package at.alladin.nettest.shared.berec.collector.api.v1.dto.lmap.control;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author fk
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "deserialize_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LmapStopDurationDto.class, name = "stop_duration"),
        @JsonSubTypes.Type(value = LmapStopEndDto.class, name = "stop_end")
})
public abstract class LmapStopDto {

}
