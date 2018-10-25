package at.alladin.nettest.shared.server.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * Contains the different network categories.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Contains the different network categories.")
public enum NetworkTypeCategory {
	MOBILE,
	WIFI,
	LAN,
	UNKNOWN
}
