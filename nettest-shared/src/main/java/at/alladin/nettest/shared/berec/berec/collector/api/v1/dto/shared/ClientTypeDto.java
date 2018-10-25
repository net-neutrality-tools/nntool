package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The type of client.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "The type of client.")
@JsonClassDescription("The type of client.")
public enum ClientTypeDto {
    MOBILE,
    BROWSER,
    DESKTOP;
}
