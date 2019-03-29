package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base class for sub measurement parameters that are sent to the measurement agent.
 * These can contain special measurement instructions (e.g. stream count, duration, timeouts, ...).
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Base class for sub measurement parameters that are sent to the measurement agent. These can contain special measurement instructions (e.g. stream count, duration, timeouts, ...).")
@JsonClassDescription("Base class for sub measurement parameters that are sent to the measurement agent. These can contain special measurement instructions (e.g. stream count, duration, timeouts, ...).")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "deserialize_type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SpeedMeasurementTypeParameters.class, name = "speed_params"),
        @JsonSubTypes.Type(value = QoSMeasurementTypeParameters.class, name = "qos_params")
})
public abstract class MeasurementTypeParameters {

}
