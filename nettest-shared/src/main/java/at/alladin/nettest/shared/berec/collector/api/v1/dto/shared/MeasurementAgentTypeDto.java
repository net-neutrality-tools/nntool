package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The type of measurement agent.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "The type of measurement agent.")
@JsonClassDescription("The type of measurement agent.")
public enum MeasurementAgentTypeDto {
    MOBILE,
    BROWSER,
    DESKTOP;
}
