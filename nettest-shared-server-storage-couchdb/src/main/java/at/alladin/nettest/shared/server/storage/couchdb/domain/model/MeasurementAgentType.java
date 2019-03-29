package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The type of agent.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("The type of agent.")
public enum MeasurementAgentType {
    MOBILE,
    BROWSER,
    DESKTOP;
}
