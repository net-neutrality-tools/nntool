package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The type of client.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@JsonClassDescription("The type of client.")
public enum ClientType {
    MOBILE,
    BROWSER,
    DESKTOP;
}
