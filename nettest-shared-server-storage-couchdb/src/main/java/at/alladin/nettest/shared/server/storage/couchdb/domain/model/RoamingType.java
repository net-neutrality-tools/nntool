package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The roaming type.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("The roaming type.")
public enum RoamingType {
	/**
	 * No roaming.
	 */
	NO_ROAMING,
	
	/**
	 * National roaming.
	 */
	NATIONAL,
	
	/**
	 * International roaming.
	 */
	INTERNATIONAL,

	/**
	 * Roaming type is not available (cannot be determined)
	 */
	NOT_AVAILABLE
}
