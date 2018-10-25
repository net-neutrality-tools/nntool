package at.alladin.nettest.shared.berec.collector.api.v1.dto.shared;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * The reason why a measurement failed.
 * 
 * @author alladin-IT GmbH (bp@alladin.at)
 *
 */
@io.swagger.annotations.ApiModel(description = "The reason why a measurement failed.")
@JsonClassDescription("The reason why a measurement failed.")
public enum ReasonDto {

	/**
	 * Use this if the connection to the measurement server couldn't be established.
	 */
	UNABLE_TO_CONNECT,
	
	/**
	 * Use this if the connection was lost during a measurement.
	 */
	CONNECTION_LOST,

	/**
	 * Use this if the network category changed (e.g. from MOBILE to WIFI).
	 */
	NETWORK_CATEGORY_CHANGED,
	
	/**
	 * Use this if the App was put to background on mobile devices.
	 */
	APP_BACKGROUNDED,
	
	/**
	 * Use this if the user aborted the measurement.
	 */
	USER_ABORTED
}
