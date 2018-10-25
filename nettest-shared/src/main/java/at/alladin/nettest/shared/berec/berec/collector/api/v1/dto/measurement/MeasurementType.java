package at.alladin.nettest.shared.berec.collector.api.v1.dto.measurement;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * Measurement type DTO.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Measurement type DTO.")
@JsonClassDescription("Measurement type DTO.")
public enum MeasurementType {
	SPEED,
	QOS;
}
