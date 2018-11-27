package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement.initiation;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * Speed measurement request options sent by the measurement agent.
 * For example, these could contain demands from the measurement agent (e.g. extended duration, streams, etc.).
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Speed measurement request options sent by the measurement agent. For example, these could contain demands from the measurement agent (e.g. extended duration, streams, etc.).")
@JsonClassDescription("Speed measurement request options sent by the measurement agent. For example, these could contain demands from the measurement agent (e.g. extended duration, streams, etc.).")
public class SpeedMeasurementRequestOptions extends MeasurementTypeRequestOptions {


}
