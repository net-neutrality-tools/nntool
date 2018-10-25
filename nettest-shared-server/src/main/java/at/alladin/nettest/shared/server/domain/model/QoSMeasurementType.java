package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * Enum that holds the names of the available QoS measurement objectives.
 *
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("Enum that holds the names of the available QoS measurement objectives.")
public enum QoSMeasurementType {
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
