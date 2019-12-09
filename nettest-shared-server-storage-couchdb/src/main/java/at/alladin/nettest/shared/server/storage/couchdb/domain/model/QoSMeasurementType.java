package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

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
    ECHO_PROTOCOL,
    SIP,

    MKIT_DASH,
    MKIT_WEB_CONNECTIVITY;
}
