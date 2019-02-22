package at.alladin.nettest.shared.server.storage.couchdb.domain.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * Measurement server type.
 * 
 * @author alladin-IT GmbH (lb@alladin.at)
 *
 */
@JsonClassDescription("Measurement server type.")
public enum MeasurementServerType {
	SPEED,
	QOS
}
