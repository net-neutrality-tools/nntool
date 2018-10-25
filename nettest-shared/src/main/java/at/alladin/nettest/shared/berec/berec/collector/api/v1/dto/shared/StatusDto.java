package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The status of a measurement.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "The status of a measurement.")
@JsonClassDescription("The status of a measurement.")
public enum StatusDto {
	STARTED,
	FINISHED,
	FAILED,
	ABORTED
}
