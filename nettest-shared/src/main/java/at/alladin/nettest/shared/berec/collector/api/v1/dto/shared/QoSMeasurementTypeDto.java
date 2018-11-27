package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * Enum that holds the names of the available QoS measurement objectives.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "Enum that holds the names of the available QoS measurement objectives.")
@JsonClassDescription("Enum that holds the names of the available QoS measurement objectives.")
public enum QoSMeasurementTypeDto {
    TCP,
    UDP,
    DNS,
    NON_TRANSPARENT_PROXY,
    HTTP_PROXY,
    VOIP,
    TRACEROUTE,
    WEBSITE,
    AUDIO,

    MKIT_DASH,
    MKIT_WEB_CONNECTIVITY;
}
